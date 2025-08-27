package com.careerpirates.resumate.folder.message.exception;

import com.careerpirates.resumate.global.message.exception.core.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FolderError implements ErrorCode {

    PARENT_FOLDER_NOT_FOUND("상위 폴더를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "FD_001"),
    PARENT_FOLDER_INVALID("자기 자신이나 자식을 상위 폴더로 설정할 수 없습니다.", HttpStatus.BAD_REQUEST, "FD_002"),
    FOLDER_NAME_INVALID("폴더 이름은 빈 문자열이거나, 50자를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST, "FD_003"),
    DISPLAY_ORDER_INVALID("폴더 표시 순서는 0 미만일 수 없습니다.", HttpStatus.BAD_REQUEST, "FD_004");

    private final String message;
    private final HttpStatus status;
    private final String code;
}
