package com.careerpirates.resumate.folder.application.service;

import com.careerpirates.resumate.folder.application.dto.request.FolderRequest;
import com.careerpirates.resumate.folder.application.dto.response.FolderResponse;
import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrasturcture.FolderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.careerpirates.resumate.folder.factory.FolderTestFactory.createDefaultFolders;
import static com.careerpirates.resumate.folder.factory.FolderTestFactory.createFolderRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@ActiveProfiles("test")
class FolderServiceTest {

    @Autowired
    private FolderService folderService;
    @Autowired
    private FolderRepository folderRepository;

    @BeforeEach
    void setUp() {
        folderRepository.saveAll(createDefaultFolders());
    }

    @AfterEach
    void tearDown() {
        folderRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("상위 폴더를 생성합니다.")
    void createFolder_success() {
        // given
        FolderRequest request = createFolderRequest(null, "NEW", 2);

        // when
        FolderResponse response = folderService.createFolder(request);

        // then
        Folder found = folderRepository.findById(response.getId()).get();
        assertThat(found).extracting("name", "order")
                .containsExactly("NEW", 2);
    }

    @Test
    @DisplayName("하위 폴더를 생성합니다.")
    void createSubFolder_success() {
        // given
        Folder parent = folderRepository.findParentFolders().get(0);
        FolderRequest request = createFolderRequest(parent.getId(), "NEW", 2);

        // when
        FolderResponse response = folderService.createFolder(request);

        // then
        assertThat(response).extracting("parentId", "parentName")
                .containsExactly(parent.getId(), parent.getName());

        Folder found = folderRepository.findById(response.getId()).get();
        assertThat(found).extracting("name", "order")
                .containsExactly("NEW", 2);
    }

    @Test
    @DisplayName("존재하지 않는 상위 폴더 선택시 예외가 발생합니다.")
    void createSubFolder_parentNotFound() {
        // given
        FolderRequest request = createFolderRequest(9999L, "NEW", 2);

        // when then
        assertThatThrownBy(() -> folderService.createFolder(request))
                .isInstanceOf(RuntimeException.class);
    }
}