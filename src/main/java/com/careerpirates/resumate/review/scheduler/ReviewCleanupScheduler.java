package com.careerpirates.resumate.review.scheduler;

import com.careerpirates.resumate.review.infrastructure.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewCleanupScheduler {

    private final ReviewRepository reviewRepository;

    // 매일 자정 실행 (cron 표현식)
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteReviewInTrashcan() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        long deletedCount = reviewRepository.deleteByIsDeletedTrueAndDeletedAtBefore(threshold);
        log.info("Deleted {} old reviews", deletedCount);
    }
}
