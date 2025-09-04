package com.careerpirates.resumate.analysis.application.service;

import com.careerpirates.resumate.analysis.application.dto.response.AnalysisResponse;
import com.careerpirates.resumate.analysis.application.dto.response.AnalysisResultDto;
import com.careerpirates.resumate.analysis.application.dto.response.GPTResponse;
import com.careerpirates.resumate.analysis.domain.Analysis;
import com.careerpirates.resumate.analysis.event.AnalysisCompletedEvent;
import com.careerpirates.resumate.analysis.event.AnalysisErrorEvent;
import com.careerpirates.resumate.analysis.infrastructure.AnalysisRepository;
import com.careerpirates.resumate.analysis.message.exception.AnalysisError;
import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrastructure.FolderRepository;
import com.careerpirates.resumate.folder.message.exception.FolderError;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.review.domain.Review;
import com.careerpirates.resumate.review.infrastructure.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ObjectMapper objectMapper;
    private final OpenAIService openAIService;
    private final FolderRepository folderRepository;
    private final ReviewRepository reviewRepository;
    private final AnalysisRepository analysisRepository;

    @Transactional
    public void requestAnalysis(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new BusinessException(FolderError.FOLDER_NOT_FOUND));

        List<Review> reviews = reviewRepository.findByFolder(folder);
        if (reviews.isEmpty()) // 분석할 폴더에 회고가 없을 경우
            throw new BusinessException(AnalysisError.FOLDER_EMPTY);

        // 분석 객체 생성 및 저장
        Analysis analysis = Analysis.builder()
                .memberId(1L) // TODO: 인증인가 구현 이후 실제 memberId 넣기
                .folderId(folderId)
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
            log.info(response.getOutput().toString());
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
        } catch (Exception e) {
            log.warn(e.getMessage());
            analysis.setError(e.getMessage());
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
    }

    @Transactional(readOnly = true)
    public AnalysisResponse getAnalysis(Long folderId) {
        Analysis analysis = analysisRepository.findTop1ByFolderIdOrderByCreatedAtDesc(folderId)
                .orElseThrow(() -> new BusinessException(AnalysisError.ANALYSIS_NOT_FOUND));

        return AnalysisResponse.of(analysis);
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
}
