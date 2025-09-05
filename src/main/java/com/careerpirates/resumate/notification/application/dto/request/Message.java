package com.careerpirates.resumate.notification.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter
@Builder
public class Message {

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotBlank
    @Length(max = 255)
    private String message;

    @URL
    @Length(max = 1024)
    private String url;
}
