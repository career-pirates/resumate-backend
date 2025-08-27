package com.careerpirates.resumate.folder.message.success;

import com.careerpirates.resumate.global.message.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FolderSuccess implements SuccessCode {

    FOLDER_CREATE_SUCCESS(HttpStatus.CREATED, "폴더 추가에 성공하였습니다");

    private final HttpStatus status;
    private final String message;
}
