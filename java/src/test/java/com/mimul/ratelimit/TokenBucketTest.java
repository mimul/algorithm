package com.mimul.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TokenBucket: 매 초마다 토큰이 capacity까지 충전; 요청마다 토큰 1개 소모
 * scaledTime = clockMillis / 1000 (초 단위)
 * Invariant: tokens > 0 → 허용 후 감소, tokens == 0 → 거부
 * Time complexity: O(1)
 */
class TokenBucketTest {

    @Test
    void allow_returnsTrue_whenTokensAvailable() {
        // Arrange: tokens=maxRequestPerSec=3으로 초기화, scaledTime=1
        TokenBucket bucket = new TokenBucket(3, () -> 1000L);

        // Act & Assert
        assertThat(bucket.allow()).isTrue();
    }

    @Test
    void allow_returnsTrue_untilAllTokensConsumed() {
        // Arrange
        TokenBucket bucket = new TokenBucket(3, () -> 1000L);

        // Act & Assert
        assertThat(bucket.allow()).isTrue();  // tokens=3→2
        assertThat(bucket.allow()).isTrue();  // tokens=2→1
        assertThat(bucket.allow()).isTrue();  // tokens=1→0
    }

    @Test
    void allow_returnsFalse_whenNoTokensLeft() {
        // Arrange: tokens 소진
        TokenBucket bucket = new TokenBucket(3, () -> 1000L);
        bucket.allow(); bucket.allow(); bucket.allow(); // tokens=0

        // Act
        boolean result = bucket.allow();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void allow_returnsTrue_afterTokensRefilled_whenOneSecondElapsed() {
        // Arrange: scaledTime=millis/1000
        AtomicLong timeMs = new AtomicLong(1000L); // scaledTime=1
        TokenBucket bucket = new TokenBucket(3, timeMs::get);
        bucket.allow(); bucket.allow(); bucket.allow(); // tokens=0
        assertThat(bucket.allow()).isFalse();

        // Act: 1초 경과 → elapsed=1, refill=1*3=3, tokens=min(0+3,3)=3
        timeMs.set(2000L);
        boolean result = bucket.allow();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void allow_capsTokensAtCapacity_whenLargeTimeElapsed() {
        // Arrange: 대량의 시간 경과 시 tokens가 capacity를 초과하지 않음
        AtomicLong timeMs = new AtomicLong(1000L);
        TokenBucket bucket = new TokenBucket(3, timeMs::get);
        bucket.allow(); // tokens=2

        // Act: 100초 경과 → refill=300, tokens=min(2+300, 3)=3
        timeMs.set(101_000L);

        // Assert: capacity=3에서 정확히 멈춤
        assertThat(bucket.allow()).isTrue();  // tokens=3→2
        assertThat(bucket.allow()).isTrue();  // tokens=2→1
        assertThat(bucket.allow()).isTrue();  // tokens=1→0
        assertThat(bucket.allow()).isFalse(); // tokens=0
    }

    @Test
    void allow_doesNotRefill_whenSameSecond() {
        // Arrange: scaledTime이 변하지 않으면 refill 없음
        TokenBucket bucket = new TokenBucket(3, () -> 1000L); // scaledTime 고정=1
        bucket.allow(); bucket.allow(); bucket.allow(); // tokens=0

        // Act: 같은 초 → refill 없음
        boolean result = bucket.allow();

        // Assert
        assertThat(result).isFalse();
    }
}
