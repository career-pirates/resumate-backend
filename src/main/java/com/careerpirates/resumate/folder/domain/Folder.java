package com.careerpirates.resumate.folder.domain;

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

    @Column(name = "name", nullable = false, columnDefinition = "CHAR(50)")
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

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
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
        this.name = name;
    }

    public void updateOrder(Integer order) {
        this.order = order;
    }

    public void changeParent(Folder parent) {
        // 기존 부모에서 제거
        if (this.parent != null) {
            this.parent.children.remove(this);
        }

        // 새로운 부모 설정
        this.parent = parent;

        // 새로운 부모에 추가
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

    @PostLoad
    private void trimNameAfterLoad() {
        if (this.name != null) {
            this.name = this.name.trim();
        }
    }
}
