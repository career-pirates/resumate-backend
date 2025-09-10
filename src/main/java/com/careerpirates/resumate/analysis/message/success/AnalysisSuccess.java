package com.careerpirates.resumate.analysis.message.success;

import com.careerpirates.resumate.global.message.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AnalysisSuccess implements SuccessCode {

    REQUEST_ANALYSIS(HttpStatus.OK, "회고 분석 요청에 성공하였습니다."),
    GET_ANALYSIS(HttpStatus.OK, "분석 결과 조회에 성공하였습니다."),
    GET_ANALYSIS_LIST(HttpStatus.OK, "분석 결과 목록 조회에 성공하였습니다."),
    ANALYSIS_REUSABLE(HttpStatus.OK, "1분 내 분석 요청하였거나, 변경사항이 없는 폴더가 있습니다."),

    // 메트릭 관련 성공 코드
    GET_METRICS_STATUS(HttpStatus.OK, "메트릭 상태 조회에 성공하였습니다."),
    SYNC_METRICS(HttpStatus.OK, "메트릭 동기화에 성공하였습니다."),
    TEST_ANALYSIS_START(HttpStatus.OK, "테스트 분석 시작에 성공하였습니다."),
    TEST_ANALYSIS_COMPLETE(HttpStatus.OK, "테스트 분석 완료에 성공하였습니다."),
    TEST_ANALYSIS_ERROR(HttpStatus.OK, "테스트 분석 에러에 성공하였습니다.");

    private final HttpStatus status;
    private final String message;
}
