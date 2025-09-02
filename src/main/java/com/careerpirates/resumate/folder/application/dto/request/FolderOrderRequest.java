package com.careerpirates.resumate.folder.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FolderOrderRequest (

    @NotNull
    Long id,

    @NotNull(message = "순서는 필수입니다")
    @Min(value = 0, message = "순서는 0 이상이어야 합니다")
    Integer order
) { }
