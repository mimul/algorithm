package com.mimul.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SlidingWindowLog: 최근 1000ms 내 요청 타임스탬프 로그로 허용 여부 결정
 * Invariant: windowLog.size() ≤ maxRequestPerSec → 허용 (추가 후 크기 기준)
 * Time complexity: O(n) where n = 윈도우 내 요청 수
 */
class SlidingWindowLogTest {

    @Test
    void allow_returnsTrue_whenFirstRequest() {
        // Arrange
        SlidingWindowLog log = new SlidingWindowLog(3, () -> 1000L);

        // Act & Assert
        assertThat(log.allow()).isTrue();
    }

    @Test
    void allow_returnsTrue_whenRequestCountEqualsLimit() {
        // Arrange
        SlidingWindowLog log = new SlidingWindowLog(3, () -> 1000L);
        log.allow(); // size=1
        log.allow(); // size=2

        // Act: size=3, 3 ≤ 3
        boolean result = log.allow();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void allow_returnsFalse_whenRequestCountExceedsLimit() {
        // Arrange
        SlidingWindowLog log = new SlidingWindowLog(3, () -> 1000L);
        log.allow(); log.allow(); log.allow(); // size=3

        // Act: size=4, 4 > 3
        boolean result = log.allow();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void allow_returnsTrue_afterOldRequestsExpireFromWindow() {
        // Arrange: 과거 요청들이 1000ms 경계 밖으로 나가면 제거됨
        AtomicLong time = new AtomicLong(1000L);
        SlidingWindowLog log = new SlidingWindowLog(3, time::get);
        log.allow(); log.allow(); log.allow(); log.allow(); // 4건 (size=4 > 3)
        assertThat(log.allow()).isFalse();

        // Act: 2001ms로 진행 → boundary=1001, t=1000인 항목 모두 제거
        time.set(2001L);
        boolean result = log.allow();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void allow_removesEntryExactlyAtBoundary_whenTimeEqualsOneSecondAfter() {
        // Arrange: boundary = now - 1000; entry at 1000 → element <= 1000 → 제거
        AtomicLong time = new AtomicLong(1000L);
        SlidingWindowLog log = new SlidingWindowLog(2, time::get);
        log.allow(); // log=[1000]

        // Act: t=2000, boundary=1000; entry(1000) ≤ 1000 → 제거, 새 항목 추가
        time.set(2000L);
        boolean result = log.allow();

        // Assert: log=[2000], size=1 ≤ 2
        assertThat(result).isTrue();
    }

    @Test
    void allow_preservesRecentEntries_whenPartialWindowExpires() {
        // Arrange
        AtomicLong time = new AtomicLong(1000L);
        SlidingWindowLog log = new SlidingWindowLog(3, time::get);
        log.allow(); // log=[1000]
        time.set(1500L);
        log.allow(); // log=[1000, 1500]

        // Act: t=2200, boundary=1200; 1000 ≤ 1200 제거, 1500 유지
        time.set(2200L);
        boolean result = log.allow(); // log=[1500, 2200], size=2 ≤ 3

        // Assert
        assertThat(result).isTrue();
    }
}
