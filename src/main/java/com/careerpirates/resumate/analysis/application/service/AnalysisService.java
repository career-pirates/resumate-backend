package com.careerpirates.resumate.analysis.application.service;

import com.careerpirates.resumate.analysis.application.dto.response.AnalysisListResponse;
import com.careerpirates.resumate.analysis.application.dto.response.AnalysisResponse;
import com.careerpirates.resumate.analysis.application.dto.response.AnalysisResultDto;
import com.careerpirates.resumate.analysis.application.dto.response.GPTResponse;
import com.careerpirates.resumate.analysis.domain.Analysis;
import com.careerpirates.resumate.analysis.domain.AnalysisStatus;
import com.careerpirates.resumate.analysis.event.AnalysisCompletedEvent;
import com.careerpirates.resumate.analysis.event.AnalysisErrorEvent;
import com.careerpirates.resumate.analysis.infrastructure.AnalysisRepository;
import com.careerpirates.resumate.analysis.message.exception.AnalysisError;
import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrastructure.FolderRepository;
import com.careerpirates.resumate.folder.message.exception.FolderError;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.member.infrastructure.MemberRepository;
import com.careerpirates.resumate.member.message.exception.MemberErrorCode;
import com.careerpirates.resumate.notification.application.dto.request.Message;
import com.careerpirates.resumate.notification.application.service.NotificationService;
import com.careerpirates.resumate.review.domain.Review;
import com.careerpirates.resumate.review.infrastructure.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ObjectMapper objectMapper;
    private final OpenAIService openAIService;
    private final NotificationService notificationService;
    private final FolderRepository folderRepository;
    private final ReviewRepository reviewRepository;
    private final AnalysisRepository analysisRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void requestAnalysis(Long folderId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        Folder folder = folderRepository.findByIdAndMember(folderId, member)
                .orElseThrow(() -> new BusinessException(FolderError.FOLDER_NOT_FOUND));

        List<Review> reviews = reviewRepository.findByFolder(folder);
        if (reviews.isEmpty()) // 분석할 폴더에 회고가 없을 경우
            throw new BusinessException(AnalysisError.FOLDER_EMPTY);

        // 1분 내 분석을 요청하였거나 폴더 내 회고 변경이 없었다면 런타임 예외 반환
        findReusableAnalysis(folder, memberId);

        // 분석 객체 생성 및 저장
        Analysis analysis = Analysis.builder()
                .memberId(memberId)
                .folderId(folderId)
                .folderName(combineFolderName(folder))
                .build();
        analysis = analysisRepository.save(analysis);

        // 사용자 입력
        String userInput = getUserInput(reviews);

        try {
            analysis.startAnalysis(userInput);
            openAIService.sendRequest(analysis.getId(), userInput);
        } catch (Exception e) {
            analysis.setError(e.getMessage());
        } finally {
            analysisRepository.save(analysis);
        }
    }

    @EventListener
    @Transactional
    public void finishAnalysis(AnalysisCompletedEvent event) {
        Long analysisId = event.getAnalysisId();
        GPTResponse response = event.getResponse();

        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new BusinessException(AnalysisError.ANALYSIS_NOT_FOUND));

        try {
            String output = response.getOutput().get(1).getContent().get(0).getText();
            analysis.setOutput(output);

            AnalysisResultDto result = objectMapper.readValue(output, AnalysisResultDto.class);
            analysis.finishAnalysis(
                    result.getSummary(),
                    result.getStrength(),
                    result.getSuggestion(),
                    result.getKeyword().toString(),
                    result.getRecKeyword().toString(),
                    response.getId(),
                    response.getUsage().getInputTokens(),
                    response.getUsage().getOutputTokens()
            );

            sendCompleteMessage(analysis);
        } catch (Exception e) {
            log.warn(e.getMessage());
            analysis.setError(e.getMessage());
            sendFailMessage(analysis);
        } finally {
            analysisRepository.save(analysis);
        }
    }

    @EventListener
    @Transactional
    public void markAsError(AnalysisErrorEvent event) {
        Long analysisId = event.getAnalysisId();

        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new BusinessException(AnalysisError.ANALYSIS_NOT_FOUND));

        analysis.setError(event.getE().getMessage());
        analysisRepository.save(analysis);

        sendFailMessage(analysis);
    }

    @Transactional(readOnly = true)
    public AnalysisResponse getAnalysis(Long folderId, Long analysisId, Long memberId) {
        Analysis analysis;
        if (analysisId == null) { // 분석 결과 ID가 없으면 최신 결과 응답
            analysis = analysisRepository.findTop1ByMemberIdAndFolderIdOrderByCreatedAtDesc(memberId, folderId)
                    .orElseThrow(() -> new BusinessException(AnalysisError.ANALYSIS_NOT_FOUND));
        }
        else { // 분석 결과 ID 제공시 해당 결과 응답
            analysis = analysisRepository.findByIdAndMemberIdAndFolderId(analysisId, memberId, folderId)
                    .orElseThrow(() -> new BusinessException(AnalysisError.ANALYSIS_NOT_FOUND));
        }

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND)
        );
        Folder folder = folderRepository.findByIdAndMember(analysis.getFolderId(), member)
                .orElse(null);

        return AnalysisResponse.of(analysis, folder);
    }

    @Transactional(readOnly = true)
    public AnalysisListResponse getAnalysisList(int page, int size, Long memberId) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<Analysis> analysisList = analysisRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND)
        );
        List<Folder> folders = folderRepository.findByIdInAndMember(
                analysisList.stream().map(Analysis::getFolderId).toList(), member
        );
        Map<Long, Folder> folderMap = folders.stream().collect(Collectors.toMap(Folder::getId, Function.identity()));

        return AnalysisListResponse.of(analysisList, folderMap);
    }

    @Transactional(readOnly = true)
    public long countTotalAnalysis(Long memberId) {
        return analysisRepository.countByMemberIdAndStatus(memberId, AnalysisStatus.SUCCESS);
    }

    private void findReusableAnalysis(Folder folder, Long memberId) {
        Optional<Analysis> reusable = analysisRepository.findTop1ByMemberIdAndFolderIdOrderByCreatedAtDesc(memberId, folder.getId())
                .filter(analysis -> isReusable(analysis, folder));

        if (reusable.isPresent())
            throw new BusinessException(AnalysisError.ANALYSIS_REUSABLE);
    }

    private boolean isReusable(Analysis analysis, Folder folder) {
        LocalDateTime now = LocalDateTime.now();
        return analysis.getCreatedAt().isAfter(now.minusMinutes(5)) &&
                (analysis.getCreatedAt().isAfter(now.minusMinutes(1)) ||
                        analysis.getCreatedAt().isAfter(folder.getModifiedAt()));
    }

    private String combineFolderName(Folder folder) {
        String parentName = folder.getParent() == null ? "" : folder.getParent().getName() + "/";
        return parentName + folder.getName();
    }

    private String getUserInput(List<Review> reviews) {
        StringBuilder inputBuilder = new StringBuilder();

        for (Review review : reviews) {
            inputBuilder.append(String.format("회고 제목: %s", review.getTitle()));
            inputBuilder.append(String.format("날짜: %s\n", review.getReviewDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

            inputBuilder.append(String.format("Like(좋았던 점):\n%s", review.getPositives()));
            inputBuilder.append(String.format("Learned(배운 점):\n%s", review.getLearnings()));
            inputBuilder.append(String.format("Lacked(개선할 점):\n%s", review.getImprovements()));
            inputBuilder.append(String.format("Longed for(원했던 점):\n%s", review.getAspirations()));
            inputBuilder.append("\n\n");
        }

        return inputBuilder.toString();
    }

    private void sendCompleteMessage(Analysis analysis) {
        notificationService.sendNotificationTo(Message.builder()
                .title("회고 분석 완료")
                .message(String.format("'%s'의 자소서 재료 분석이 완료되었습니다!", analysis.getFolderName()))
                .build(),
                analysis.getMemberId()
        );
    }

    private void sendFailMessage(Analysis analysis) {
        notificationService.sendNotificationTo(Message.builder()
                .title("회고 분석 실패")
                .message(String.format("'%s'의 자소서 재료 분석 중 오류가 발생하였습니다.", analysis.getFolderName()))
                .build(),
                analysis.getMemberId()
        );
    }
}
