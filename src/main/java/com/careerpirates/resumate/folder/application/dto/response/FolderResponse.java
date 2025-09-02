package com.careerpirates.resumate.folder.application.dto.response;

import com.careerpirates.resumate.folder.domain.Folder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FolderResponse {

    private Long id;
    private Long parentId;
    private String parentName;
    private String name;
    private Integer order;
    private LocalDateTime modifiedAt;

    public static FolderResponse of(Folder folder) {
        Folder parent = folder.getParent();

        return FolderResponse.builder()
                .id(folder.getId())
                .name(folder.getName())
                .order(folder.getOrder())
                .modifiedAt(folder.getModifiedAt())
                .parentId(parent == null ? null : parent.getId())
                .parentName(parent == null ? null : parent.getName())
                .build();
    }
}
