package com.careerpirates.resumate.folder.application.service;

import com.careerpirates.resumate.folder.application.dto.request.FolderNameRequest;
import com.careerpirates.resumate.folder.application.dto.request.FolderOrderRequest;
import com.careerpirates.resumate.folder.application.dto.request.FolderRequest;
import com.careerpirates.resumate.folder.application.dto.response.FolderResponse;
import com.careerpirates.resumate.folder.application.dto.response.FolderTreeResponse;
import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrastructure.FolderRepository;
import com.careerpirates.resumate.folder.message.exception.FolderError;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.member.domain.enums.OAuthProvider;
import com.careerpirates.resumate.member.infrastructure.MemberRepository;
import com.careerpirates.resumate.review.application.dto.response.ReviewResponse;
import com.careerpirates.resumate.review.application.service.ReviewService;
import com.careerpirates.resumate.review.domain.Review;
import com.careerpirates.resumate.review.infrastructure.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.careerpirates.resumate.folder.factory.FolderTestFactory.createDefaultFolders;
import static com.careerpirates.resumate.folder.factory.FolderTestFactory.createFolderRequest;
import static com.careerpirates.resumate.member.factory.MemberFactory.createMember;
import static com.careerpirates.resumate.review.factory.ReviewTestFactory.createReviewRequest;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
//@ActiveProfiles("test")
class FolderServiceTest {

    @Autowired
    private FolderService folderService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(createMember("test"));
        List<Folder> folders = folderRepository.saveAll(createDefaultFolders(member));
    }

    @Test
    @DisplayName("상위 폴더를 생성합니다.")
    void createFolder_success() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        FolderRequest request = createFolderRequest(null, "NEW", 2);

        // when
        FolderResponse response = folderService.createFolder(request, member.getId());

        // then
        Folder found = folderRepository.findByIdAndMember(response.getId(), member).get();
        assertThat(found).extracting("name", "order")
                .containsExactly("NEW", 2);
    }

    @Test
    @DisplayName("하위 폴더를 생성합니다.")
    void createSubFolder_success() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();

        Folder parent = folderRepository.findParentFolders(member).get(0);
        FolderRequest request = createFolderRequest(parent.getId(), "NEW", 2);

        // when
        FolderResponse response = folderService.createFolder(request, member.getId());

        // then
        assertThat(response).extracting("parentId", "parentName")
                .containsExactly(parent.getId(), parent.getName());

        Folder found = folderRepository.findByIdAndMember(response.getId(), member).get();
        assertThat(found).extracting("name", "order")
                .containsExactly("NEW", 2);
    }

    @Test
    @DisplayName("존재하지 않는 상위 폴더 선택시 예외가 발생합니다.")
    void createSubFolder_parentNotFound() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        FolderRequest request = createFolderRequest(9999L, "NEW", 2);

        // when then
        assertThatThrownBy(() -> folderService.createFolder(request, member.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(FolderError.PARENT_FOLDER_NOT_FOUND);
    }

    @Test
    @DisplayName("폴더 이름을 수정합니다.")
    void updateFolderName() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        Folder folder = folderRepository.findParentFolders(member).get(0);

        // when
        FolderResponse response = folderService.updateFolderName(
                folder.getId(),
                FolderNameRequest.builder().name("새이름").build(),
                member.getId()
        );

        // then
        Folder found = folderRepository.findById(response.getId()).get();
        assertThat(found).extracting("name")
                .isEqualTo(response.getName())
                .isEqualTo("새이름");
    }

    @Test
    @DisplayName("상위 폴더를 삭제합니다.")
    void deleteFolder_success() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        Folder folderA = folderRepository.findByName("A").get();

        // when
        folderService.deleteFolder(folderA.getId(), member.getId());

        // then
        Optional<Folder> resultA = folderRepository.findByName("A");
        assertThat(resultA.isPresent()).isFalse();
        Optional<Folder> resultAB = folderRepository.findByName("AB");
        assertThat(resultAB.isPresent()).isFalse();
        Optional<Folder> resultB = folderRepository.findByName("B");
        assertThat(resultB.isPresent()).isTrue();
        Optional<Folder> resultBA = folderRepository.findByName("BA");
        assertThat(resultBA.isPresent()).isTrue();
    }

    @Test
    @DisplayName("하위 폴더를 삭제합니다.")
    void deleteSubFolder_success() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        Folder folderAA = folderRepository.findByName("AA").get();
        ReviewResponse review = reviewService.createReview(
                createReviewRequest(folderAA.getId(), "회고AA", true, LocalDate.of(2025, 9, 15))
        );

        // when
        folderService.deleteFolder(folderAA.getId(), member.getId());

        // then
        Optional<Folder> resultA = folderRepository.findByName("A");
        assertThat(resultA.isPresent()).isTrue();
        Optional<Folder> resultAA = folderRepository.findByName("AA");
        assertThat(resultAA.isPresent()).isFalse();
        Optional<Folder> resultAB = folderRepository.findByName("AB");
        assertThat(resultAB.isPresent()).isTrue();
        Review foundReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertThat(foundReview).extracting("title", "isDeleted")
                .containsExactly("회고AA", true);
    }

    @Test
    @DisplayName("상위 폴더와 하위 폴더 목록을 조회합니다.")
    void getFolders_success() {
        // when
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        List<FolderTreeResponse> result = folderService.getFolders(member.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).extracting("name").isEqualTo("A");
        assertThat(result.get(0).getChildren())
                .extracting("name")
                .containsExactly("AA", "AB");
        assertThat(result.get(1)).extracting("name").isEqualTo("B");
        assertThat(result.get(1).getChildren())
                .extracting("name")
                .containsExactly("BA", "BB");
    }

    @Test
    @DisplayName("상위 폴더 사이의 표시 순서를 설정합니다.")
    void setFolderOrder_success() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        Folder folderA = folderRepository.findByName("A").get();
        Folder folderB = folderRepository.findByName("B").get();

        List<FolderOrderRequest> request = List.of(
                FolderOrderRequest.builder().id(folderA.getId()).order(2).build(),
                FolderOrderRequest.builder().id(folderB.getId()).order(1).build()
        );

        // when
        folderService.setFolderOrder(request, member.getId());

        // then
        assertThat(folderRepository.findParentFolders(member))
                .extracting("name")
                .containsExactly("B", "A");
    }

    @Test
    @DisplayName("상위 폴더 순서 설정시 존재하지 않으면 예외 발생합니다.")
    void setFolderOrder_notFound() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        List<FolderOrderRequest> request = List.of(
                FolderOrderRequest.builder().id(9999L).order(1).build()
        );

        // when then
        assertThatThrownBy(() -> folderService.setFolderOrder(request, member.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(FolderError.PARENT_FOLDER_NOT_FOUND);
    }

    @Test
    @DisplayName("폴더의 상위 폴더를 변경합니다.")
    void setSubFolderTree_success() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();

        Folder folderA = folderRepository.findByName("A").get();
        Folder folderAA = folderRepository.findByName("AA").get();
        Folder folderAB = folderRepository.findByName("AB").get();
        Folder folderBA = folderRepository.findByName("BA").get();

        List<FolderOrderRequest> request = List.of(
                FolderOrderRequest.builder().id(folderAA.getId()).order(2).build(),
                FolderOrderRequest.builder().id(folderAB.getId()).order(3).build(),
                FolderOrderRequest.builder().id(folderBA.getId()).order(1).build()
        );

        // when
        folderService.setSubFolderTree(folderA.getId(), request, member.getId());

        // then
        List<FolderTreeResponse> foundFolders = folderService.getFolders(member.getId());
        assertThat(childrenOf(foundFolders, "A"))
                .extracting("name")
                .containsExactly("BA", "AA", "AB");

        assertThat(childrenOf(foundFolders, "B"))
                .extracting("name")
                .containsExactly("BB");
    }

    @Test
    @DisplayName("폴더를 3 중첩 시도시 예외가 발생합니다.")
    void setSubFolderTree_maxNested() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        Folder folderA = folderRepository.findByName("A").get();
        Folder folderB = folderRepository.findByName("B").get();

        List<FolderOrderRequest> request = List.of(
                FolderOrderRequest.builder().id(folderB.getId()).order(1).build()
        );

        // when then
        assertThatThrownBy(() -> folderService.setSubFolderTree(folderA.getId(), request, member.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(FolderError.MAX_NESTING_DEPTH_EXCEEDED);
    }

    @Test
    @DisplayName("자기 자신을 상위 폴더로 선택시 예외가 발생합니다.")
    void setSubFolderTree_selfNested() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        Folder folderA = folderRepository.findByName("A").get();

        List<FolderOrderRequest> request = List.of(
                FolderOrderRequest.builder().id(folderA.getId()).order(1).build()
        );

        // when then
        assertThatThrownBy(() -> folderService.setSubFolderTree(folderA.getId(), request, member.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(FolderError.PARENT_FOLDER_INVALID);
    }

    @Test
    @DisplayName("자신의 하위폴더를 상위 폴더로 선택시 예외가 발생합니다.")
    void setSubFolderTree_nestedCycle() {
        // given
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        Folder folderA = folderRepository.findByName("A").get();
        Folder folderAB = folderRepository.findByName("AB").get();

        List<FolderOrderRequest> request = List.of(
                FolderOrderRequest.builder().id(folderA.getId()).order(1).build()
        );

        // when then
        assertThatThrownBy(() -> folderService.setSubFolderTree(folderAB.getId(), request, member.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(FolderError.PARENT_FOLDER_INVALID);
    }

    private List<FolderTreeResponse> childrenOf(List<FolderTreeResponse> folders, String name) {
        return folders.stream()
                .filter(f -> f.getName().equals(name))
                .findFirst()
                .map(FolderTreeResponse::getChildren)
                .orElseThrow();
    }
}
