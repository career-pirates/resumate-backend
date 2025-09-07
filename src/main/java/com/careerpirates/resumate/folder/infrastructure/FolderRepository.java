package com.careerpirates.resumate.folder.infrastructure;

import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findByIdAndMember(Long id, Member member);

    @Query("""
        SELECT distinct fd FROM Folder fd
        WHERE fd.member = :member
          AND fd.parent IS NULL
        ORDER BY fd.order
    """)
    List<Folder> findParentFolders(Member member);

    List<Folder> findByIdInAndMember(List<Long> ids, Member member);

    Optional<Folder> findByNameAndMember(String name, Member member);
}
