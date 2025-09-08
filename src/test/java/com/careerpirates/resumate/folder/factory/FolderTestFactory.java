package com.careerpirates.resumate.folder.factory;

import com.careerpirates.resumate.folder.application.dto.request.FolderRequest;
import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.member.domain.entity.Member;

import java.util.List;

public class FolderTestFactory {

    public static Folder createFolder(Folder parent, Member member, String name, int order) {
        return Folder.builder()
                .parent(parent)
                .member(member)
                .name(name)
                .order(order)
                .build();
    }

    public static List<Folder> createDefaultFolders(Member member) {
        Folder folderA = createFolder(null, member, "A", 0);
        Folder folderB = createFolder(null, member, "B", 1);

        Folder subFolderAA = createFolder(folderA, member, "AA", 0);
        Folder subFolderAB = createFolder(folderA, member, "AB", 1);
        Folder subFolderBA = createFolder(folderB, member, "BA", 0);
        Folder subFolderBB = createFolder(folderB, member, "BB", 1);

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
