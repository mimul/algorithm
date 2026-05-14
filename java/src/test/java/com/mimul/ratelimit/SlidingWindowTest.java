package com.mimul.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SlidingWindow: 현재 윈도우와 이전 윈도우의 가중치 합으로 허용 여부 결정
 *
 * LARGE_WINDOW_MS=1_000_000: preWindowKey가 항상 음수 → preCount=null
 * → 현재 윈도우 카운트만 검사하는 단순 경로 테스트
 *
 * UNIT_WINDOW_MS=1: curWindowKey=now, preWindowKey=now-1000
 * → time=0 요청 후 time=1000으로 전진하면 preWindowKey=0이 맵 엔트리를 찾아
 *    preCount != null 경로(가중치 합 계산)를 실행
 */
class SlidingWindowTest {

    private static final int LARGE_WINDOW_MS = 1_000_000;
    private static final int UNIT_WINDOW_MS = 1;

    @Test
    void allow_returnsTrue_whenFirstRequestInWindow() {
        // Arrange
        SlidingWindow sw = new SlidingWindow(3, LARGE_WINDOW_MS, () -> 0L);

        // Act & Assert
        assertThat(sw.allow()).isTrue();
    }

    @Test
    void allow_returnsTrue_whenRequestCountEqualsLimit() {
        // Arrange
        SlidingWindow sw = new SlidingWindow(3, LARGE_WINDOW_MS, () -> 0L);
        sw.allow(); // count=1
        sw.allow(); // count=2

        // Act: count=3
        boolean result = sw.allow();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void allow_returnsFalse_whenRequestCountExceedsLimit() {
        // Arrange
        SlidingWindow sw = new SlidingWindow(3, LARGE_WINDOW_MS, () -> 0L);
        sw.allow(); sw.allow(); sw.allow(); // count=3

        // Act: count=4 > 3
        boolean result = sw.allow();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void allow_returnsTrue_inNewWindow_afterPreviousWindowExhausted() {
        // Arrange: windowSizeInMs=1_000_000 → window 0 at t=0, window 1 at t=1_000_000
        AtomicLong time = new AtomicLong(0L);
        SlidingWindow sw = new SlidingWindow(2, LARGE_WINDOW_MS, time::get);
        sw.allow(); sw.allow();
        assertThat(sw.allow()).isFalse(); // window 0 exhausted

        // Act: window 1 (curWindowKey=1, preWindowKey=-999 → null)
        time.set(1_000_000L);
        boolean result = sw.allow();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void allow_maintainsSeparateCount_perWindow() {
        // Arrange
        AtomicLong time = new AtomicLong(0L);
        SlidingWindow sw = new SlidingWindow(2, LARGE_WINDOW_MS, time::get);

        // window 0
        assertThat(sw.allow()).isTrue();  // count=1
        assertThat(sw.allow()).isTrue();  // count=2
        assertThat(sw.allow()).isFalse(); // count=3 > 2

        // window 1: 독립적인 카운트
        time.set(1_000_000L);
        assertThat(sw.allow()).isTrue();  // count=1
        assertThat(sw.allow()).isTrue();  // count=2
        assertThat(sw.allow()).isFalse(); // count=3 > 2
    }

    @Test
    void allow_returnsTrue_whenWeightedPreviousWindowCountIsWithinLimit() {
        // Arrange: UNIT_WINDOW_MS=1 → curWindowKey=now, preWindowKey=now-1000
        // time=0: window[0]에 2개 누적
        AtomicLong time = new AtomicLong(0L);
        SlidingWindow sw = new SlidingWindow(10, UNIT_WINDOW_MS, time::get);
        sw.allow(); sw.allow(); // window[0].count=2

        // Act: time=1000 → curWindowKey=1000, preWindowKey=0(존재) → preCount=2
        // preWeight = 1 - (1000 - 1000) / 1000.0 = 1.0
        // count = 2 * 1.0 + 1 = 3 ≤ 10
        time.set(1000L);
        boolean result = sw.allow();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void allow_returnsFalse_whenWeightedPreviousWindowPushesCountOverLimit() {
        // Arrange: limit=5, window[0]에 5개 누적
        AtomicLong time = new AtomicLong(0L);
        SlidingWindow sw = new SlidingWindow(5, UNIT_WINDOW_MS, time::get);
        for (int i = 0; i < 5; i++) sw.allow(); // window[0].count=5

        // Act: time=1000 → preCount=5, preWeight=1.0
        // count = 5 * 1.0 + 1 = 6 > 5
        time.set(1000L);
        boolean result = sw.allow();

        // Assert
        assertThat(result).isFalse();
    }
}
