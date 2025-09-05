package com.careerpirates.resumate.notification.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
public class Message {

    @Length(max = 50)
    private String title;
    @Length(max = 255)
    private String message;
    @Length(max = 1024)
    private String url;
}
