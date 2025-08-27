package com.careerpirates.resumate.folder.message.exception;

import com.careerpirates.resumate.global.message.exception.core.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FolderError implements ErrorCode {

    PARENT_FOLDER_NOT_FOUND("상위 폴더를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "FD_001");

    private final String message;
    private final HttpStatus status;
    private final String code;
}
