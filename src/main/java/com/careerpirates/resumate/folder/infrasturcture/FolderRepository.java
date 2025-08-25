package com.careerpirates.resumate.folder.infrasturcture;

import com.careerpirates.resumate.folder.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("SELECT fd from Folder fd WHERE fd.parent IS NULL ORDER BY fd.order")
    List<Folder> findParentFolders();

}
