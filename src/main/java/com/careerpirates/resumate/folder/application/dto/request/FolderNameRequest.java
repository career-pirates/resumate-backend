package com.careerpirates.resumate.folder.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record FolderNameRequest(

    @NotBlank(message = "폴더명은 필수입니다")
    @Size(max = 50, message = "폴더명은 50자 이하로 입력해주세요")
    String name
) {}
