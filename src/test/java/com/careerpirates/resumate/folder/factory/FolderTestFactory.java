package com.careerpirates.resumate.folder.factory;

import com.careerpirates.resumate.folder.application.dto.request.FolderRequest;
import com.careerpirates.resumate.folder.domain.Folder;

import java.util.List;

public class FolderTestFactory {

    public static Folder createFolder(Folder parent, String name, int order) {
        return Folder.builder()
                .parent(parent)
                .name(name)
                .order(order)
                .build();
    }

    public static List<Folder> createDefaultFolders() {
        Folder folderA = createFolder(null, "A", 0);
        Folder folderB = createFolder(null, "B", 1);

        Folder subFolderAA = createFolder(folderA, "AA", 0);
        Folder subFolderAB = createFolder(folderA, "AB", 1);
        Folder subFolderBA = createFolder(folderA, "BA", 0);
        Folder subFolderBB = createFolder(folderA, "BB", 1);

        return List.of(folderA, folderB, subFolderAA, subFolderAB, subFolderBA, subFolderBB);
    }

    public static FolderRequest createFolderRequest(Long parentId, String name, Integer order) {
        return FolderRequest.builder()
                .parentId(parentId)
                .name(name)
                .order(order)
                .build();
    }
}
