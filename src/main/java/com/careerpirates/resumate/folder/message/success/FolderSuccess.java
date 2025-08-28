package com.careerpirates.resumate.folder.message.success;

import com.careerpirates.resumate.global.message.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FolderSuccess implements SuccessCode {

    CREATE_FOLDER(HttpStatus.CREATED, "폴더 추가에 성공하였습니다."),
    UPDATE_FOLDER_NAME(HttpStatus.OK, "폴더 이름 변경에 성공하였습니다."),
    DELETE_FOLDER(HttpStatus.OK, "폴더 삭제에 성공하였습니다."),
    GET_FOLDERS(HttpStatus.OK, "폴더 목록 조회에 성공하였습니다."),
    SET_FOLDER_ORDER(HttpStatus.OK, "상위 폴더 순서 설정에 성공하였습니다.");

    private final HttpStatus status;
    private final String message;
}
