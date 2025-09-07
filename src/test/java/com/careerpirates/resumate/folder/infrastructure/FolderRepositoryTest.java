package com.careerpirates.resumate.folder.infrastructure;

import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.member.domain.enums.OAuthProvider;
import com.careerpirates.resumate.member.infrastructure.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.careerpirates.resumate.folder.factory.FolderTestFactory.createDefaultFolders;
import static com.careerpirates.resumate.member.factory.MemberFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
//@ActiveProfiles("test")
class FolderRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(createMember("test"));
        folderRepository.saveAll(createDefaultFolders(member));
    }

    @Test
    @DisplayName("상위 폴더 조회 성공")
    void findParentFolders_success() {
        // when
        Member member = memberRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "1").orElseThrow();
        List<Folder> parentFolders = folderRepository.findParentFolders(member);

        // then
        assertThat(parentFolders).hasSize(2)
                .extracting("name")
                .containsExactly("A", "B");
    }
}
