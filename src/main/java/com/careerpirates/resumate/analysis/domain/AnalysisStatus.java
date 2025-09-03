package com.careerpirates.resumate.analysis.domain;

public enum AnalysisStatus {
    IDLE,      // 초기 상태, 아무 작업도 수행되지 않음
    PENDING,   // 분석 요청이 처리 대기 중
    SUCCESS,   // 분석 완료 및 성공
    ERROR      // 분석 중 에러 발생
}
