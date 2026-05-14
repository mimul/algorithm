package com.mimul.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FixedWindowCounter: 고정 시간 윈도우 내 요청 횟수로 허용 여부 결정
 * Invariant: 윈도우 내 count ≤ maxRequestPerSec → 허용, 초과 → 거부
 * Time complexity: O(1)
 */
class FixedWindowCounterTest {

    @Test
    void allow_returnsTrue_whenFirstRequestInWindow() {
        // Arrange
        FixedWindowCounter counter = new FixedWindowCounter(3, 1000, () -> 1000L);

        // Act & Assert
        assertThat(counter.allow()).isTrue();
    }

    @Test
    void allow_returnsTrue_whenRequestCountEqualsLimit() {
        // Arrange
        FixedWindowCounter counter = new FixedWindowCounter(3, 1000, () -> 1000L);
        counter.allow(); // count=1
        counter.allow(); // count=2

        // Act
        boolean result = counter.allow(); // count=3

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void allow_returnsFalse_whenRequestCountExceedsLimit() {
        // Arrange
        FixedWindowCounter counter = new FixedWindowCounter(3, 1000, () -> 1000L);
        counter.allow(); // count=1
        counter.allow(); // count=2
        counter.allow(); // count=3

        // Act
        boolean result = counter.allow(); // count=4 > 3

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void allow_returnsTrue_whenNewWindowStartsAfterPreviousWindowLimitReached() {
        // Arrange
        AtomicLong time = new AtomicLong(1000L);
        FixedWindowCounter counter = new FixedWindowCounter(3, 1000, time::get);
        counter.allow(); counter.allow(); counter.allow();
        assertThat(counter.allow()).isFalse(); // limit exhausted in window 1

        // Act: advance to window 2
        time.set(2000L);
        boolean result = counter.allow();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void allow_maintainsSeparateCount_perWindow() {
        // Arrange
        AtomicLong time = new AtomicLong(0L);
        FixedWindowCounter counter = new FixedWindowCounter(2, 1000, time::get);

        // window 0: capacity=2
        assertThat(counter.allow()).isTrue();  // count=1
        assertThat(counter.allow()).isTrue();  // count=2
        assertThat(counter.allow()).isFalse(); // count=3 > 2

        // Act: window 1
        time.set(1000L);

        // Assert: fresh count in new window
        assertThat(counter.allow()).isTrue();  // count=1
        assertThat(counter.allow()).isTrue();  // count=2
        assertThat(counter.allow()).isFalse(); // count=3 > 2
    }

    @Test
    void toString_containsWindowEntry_whenRequestsRecorded() {
        // Arrange
        FixedWindowCounter counter = new FixedWindowCounter(3, 1000, () -> 1000L);
        counter.allow();

        // Act
        String result = counter.toString();

        // Assert
        assertThat(result).contains("-->");
    }
}
