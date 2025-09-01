package com.careerpirates.resumate.review.application.dto.request;

import org.springframework.data.domain.Sort;

public enum ReviewSortType {

    /**
     * 회고 날짜 기준 내림차순 정렬
     * → 가장 최근 회고일 순으로 조회
     */
    REVIEW_DATE_DESC(Sort.by(Sort.Direction.DESC, "reviewDate")),

    /**
     * 회고 날짜 기준 오름차순 정렬
     * → 가장 오래된 회고일 순으로 조회
     */
    REVIEW_DATE_ASC(Sort.by(Sort.Direction.ASC, "reviewDate")),

    /**
     * 수정일 기준 내림차순 정렬
     * → 최근에 수정된 순으로 조회
     */
    MODIFIED_DATE_DESC(Sort.by(Sort.Direction.DESC, "modifiedAt")),

    /**
     * 수정일 기준 오름차순 정렬
     * → 가장 예전에 수정된 순으로 조회
     */
    MODIFIED_DATE_ASC(Sort.by(Sort.Direction.ASC, "modifiedAt"));

    private final Sort sort;

    ReviewSortType(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() {
        return sort;
    }
}
