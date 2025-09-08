package com.careerpirates.resumate.folder.application.dto.response;

import com.careerpirates.resumate.folder.domain.Folder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class FolderTreeResponse {

    private Long id;
    private Long parentId;
    private String parentName;
    private String name;
    private Integer order;
    private LocalDateTime modifiedAt;
    private List<FolderTreeResponse> children;

    public static FolderTreeResponse of(Folder folder, boolean children) {
        Folder parent = folder.getParent();

        return FolderTreeResponse.builder()
                .id(folder.getId())
                .name(folder.getName())
                .order(folder.getOrder())
                .modifiedAt(folder.getModifiedAt())
                .parentId(parent == null ? null : parent.getId())
                .parentName(parent == null ? null : parent.getName())
                .children(children
                        ? folder.getChildren().stream()
                            .sorted(Comparator.comparingInt(Folder::getOrder))
                            .map(fd -> FolderTreeResponse.of(fd, children))
                            .toList()
                        : null
                )
                .build();
    }
}
