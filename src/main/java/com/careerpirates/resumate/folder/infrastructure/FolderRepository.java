package com.careerpirates.resumate.folder.infrastructure;

import com.careerpirates.resumate.folder.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("SELECT fd from Folder fd WHERE fd.parent IS NULL ORDER BY fd.order")
    List<Folder> findParentFolders();

    Optional<Folder> findByName(String name);
}
