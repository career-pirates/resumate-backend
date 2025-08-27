package com.careerpirates.resumate.folder.domain;

import com.careerpirates.resumate.folder.message.exception.FolderError;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "folder")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "display_order", nullable = false)
    private Integer order;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    // 자기 참조 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Folder> children = new ArrayList<>();

    @Builder
    public Folder(Folder parent, String name, Integer order) {
        this.parent = parent;
        this.name = name;
        this.order = order;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(FolderError.FOLDER_NAME_INVALID);
        }
        this.name = name;
    }

    public void updateOrder(int order) {
        if (order < 0) {
            throw new BusinessException(FolderError.DISPLAY_ORDER_INVALID);
        }
        this.order = order;
    }

    public void changeParent(Folder parent) {
        // 동일 상위 폴더
        if (this.parent == parent)
            return;
        // 자기 자신을 상위 폴더로 지정할 수 없음
        if (this == parent)
            throw new BusinessException(FolderError.PARENT_FOLDER_INVALID);
        // 하위 폴더를 상위 폴더로 지정할 수 없음
        for (Folder child : this.children) {
            if (child == parent)
                throw new BusinessException(FolderError.PARENT_FOLDER_INVALID);
        }

        // 기존 부모에서 제거
        if (this.parent != null) {
            this.parent.children.remove(this);
        }

        // 새로운 부모 설정
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    public void addChild(Folder child) {
        children.add(child);
        child.parent = this;
    }

    public void removeChild(Folder child) {
        children.remove(child);
        child.parent = null;
    }
}
