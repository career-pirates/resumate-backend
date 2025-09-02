package com.careerpirates.resumate.review.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ReviewRequest (

        @NotNull(message = "폴더는 필수입니다")
        Long folderId,

        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 100, message = "제목은 100자 이하로 입력해주세요")
        String title,

        @Size(max = 500, message = "좋았던 점은 500자 이하로 입력해주세요")
        String positives,

        @Size(max = 500, message = "개선할 점은 500자 이하로 입력해주세요")
        String improvements,

        @Size(max = 500, message = "배운 점은 500자 이하로 입력해주세요")
        String learnings,

        @Size(max = 500, message = "원했던 점은 500자 이하로 입력해주세요")
        String aspirations,

        @NotNull(message = "완료 상태는 필수입니다")
        Boolean isCompleted,

        @NotNull(message = "회고 날짜는 필수입니다")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate reviewDate
) { }
