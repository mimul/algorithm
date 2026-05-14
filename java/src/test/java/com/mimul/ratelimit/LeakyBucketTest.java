package com.mimul.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LeakyBucket: 요청이 버킷을 채우고 시간이 지남에 따라 일정 속도로 누수
 * capacity=N, leakInterval=1000/N ms
 * Invariant: used >= capacity → 거부, used < capacity → 허용
 *
 * 구현 특이사항: used >= capacity (>= 이므로 실제 허용은 capacity-1건)
 */
class LeakyBucketTest {

    // capacity=3: used=1(true), used=2(true), used=3(>=3, false)
    @Test
    void allow_returnsTrue_whenBucketHasCapacity() {
        // Arrange: capacity=3, leakInterval=333ms, 시각 고정
        LeakyBucket bucket = new LeakyBucket(3, () -> 1000L);

        // Act & Assert
        assertThat(bucket.allow()).isTrue(); // used=1
        assertThat(bucket.allow()).isTrue(); // used=2
    }

    @Test
    void allow_returnsFalse_whenBucketReachesCapacity() {
        // Arrange
        LeakyBucket bucket = new LeakyBucket(3, () -> 1000L);
        bucket.allow(); // used=1
        bucket.allow(); // used=2

        // Act: used=3 >= capacity=3
        boolean result = bucket.allow();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void allow_returnsTrue_afterLeakReducesUsedBelowCapacity() {
        // Arrange: capacity=3, leakInterval=1000/3=333ms
        AtomicLong time = new AtomicLong(1000L);
        LeakyBucket bucket = new LeakyBucket(3, time::get);
        bucket.allow(); // used=1
        bucket.allow(); // used=2
        // 시각 334ms 진행: leaks=334/333=1, used=2-1=1
        time.set(1334L);

        // Act
        boolean result = bucket.allow(); // used=2, 2 < 3 → true

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void allow_resetsUsedToZero_whenElapsedTimeExceedsAccumulatedRequests() {
        // Arrange: capacity=3, leakInterval=333ms
        AtomicLong time = new AtomicLong(1000L);
        LeakyBucket bucket = new LeakyBucket(3, time::get);
        bucket.allow(); // used=1
        bucket.allow(); // used=2
        // 2000ms 진행: leaks=2000/333=6, used=2 ≤ 6 → used=0
        time.set(3000L);

        // Act
        boolean result = bucket.allow(); // used=1, 1 < 3 → true

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void allow_doesNotLeak_whenTimeDoesNotAdvance() {
        // Arrange: same timestamp → lastLeakTime unchanged, no leak
        LeakyBucket bucket = new LeakyBucket(5, () -> 1000L);
        // used=1,2,3,4 all allowed; used=5 >= capacity=5 → false
        bucket.allow(); bucket.allow(); bucket.allow(); bucket.allow();

        // Act
        boolean result = bucket.allow(); // used=5 >= 5

        // Assert
        assertThat(result).isFalse();
    }
}
