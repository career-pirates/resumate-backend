package com.careerpirates.resumate.folder.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FolderRequest {

    private Long parentId;

    @NotBlank(message = "폴더명은 필수입니다")
    @Size(max = 50, message = "폴더명은 50자 이하로 입력해주세요")
    private String name;

    @NotNull(message = "순서는 필수입니다")
    @Min(value = 0, message = "순서는 0 이상이어야 합니다")
    private Integer order;
}
