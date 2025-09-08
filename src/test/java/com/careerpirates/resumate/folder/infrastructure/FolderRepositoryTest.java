package com.careerpirates.resumate.folder.infrastructure;

import com.careerpirates.resumate.folder.domain.Folder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.careerpirates.resumate.folder.factory.FolderTestFactory.createDefaultFolders;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
class FolderRepositoryTest {

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
    @DisplayName("상위 폴더 조회 성공")
    void findParentFolders_success() {
        // when
        List<Folder> parentFolders = folderRepository.findParentFolders();

        // then
        assertThat(parentFolders).hasSize(2)
                .extracting("name")
                .containsExactly("A", "B");
    }
}
