#include "ring_buffer.h"

#include <gtest/gtest.h>

#include <atomic>
#include <stdexcept>
#include <thread>
#include <vector>

// ────────────────────────────────────────────────
// Constructor Invariant
// ────────────────────────────────────────────────

TEST(RingBufferTest, constructor_throws_whenSizeIsZeroOrNotPowerOfTwo) {
  EXPECT_THROW(RingBuffer(0), std::invalid_argument);
  EXPECT_THROW(RingBuffer(3), std::invalid_argument);
  EXPECT_THROW(RingBuffer(6), std::invalid_argument);
  EXPECT_NO_THROW(RingBuffer(1));
  EXPECT_NO_THROW(RingBuffer(4));
  EXPECT_NO_THROW(RingBuffer(1024));
}

// ────────────────────────────────────────────────
// Empty Invariant
// ────────────────────────────────────────────────

TEST(RingBufferTest, dequeue_returnsFalse_whenBufferIsEmpty) {
  RingBuffer rb(4);

  int dest;
  EXPECT_FALSE(rb.dequeue(&dest));
}

TEST(RingBufferTest, dequeue_doesNotModifyDest_whenBufferIsEmpty) {
  RingBuffer rb(4);
  int dest = -999;

  EXPECT_FALSE(rb.dequeue(&dest));

  EXPECT_EQ(dest, -999);
}

// ────────────────────────────────────────────────
// Full Invariant
// ────────────────────────────────────────────────

TEST(RingBufferTest, enqueue_returnsFalse_whenBufferIsFull) {
  RingBuffer rb(4);
  for (int i = 0; i < 4; ++i) rb.enqueue(i);

  EXPECT_FALSE(rb.enqueue(99));
}

TEST(RingBufferTest, enqueue_doesNotOverwriteExistingItems_whenBufferIsFull) {
  RingBuffer rb(4);
  for (int i = 1; i <= 4; ++i) ASSERT_TRUE(rb.enqueue(i));
  ASSERT_FALSE(rb.enqueue(99));  // 거부 확인

  int vals[4];
  for (int& v : vals) ASSERT_TRUE(rb.dequeue(&v));
  EXPECT_EQ(vals[0], 1);
  EXPECT_EQ(vals[1], 2);
  EXPECT_EQ(vals[2], 3);
  EXPECT_EQ(vals[3], 4);
}

// ────────────────────────────────────────────────
// FIFO Invariant
// ────────────────────────────────────────────────

TEST(RingBufferTest, dequeue_returnsValuesInEnqueueOrder_whenSequentiallyAccessed) {
  RingBuffer rb(4);
  ASSERT_TRUE(rb.enqueue(10));
  ASSERT_TRUE(rb.enqueue(20));
  ASSERT_TRUE(rb.enqueue(30));

  int a, b, c;
  EXPECT_TRUE(rb.dequeue(&a));
  EXPECT_TRUE(rb.dequeue(&b));
  EXPECT_TRUE(rb.dequeue(&c));
  EXPECT_EQ(a, 10);
  EXPECT_EQ(b, 20);
  EXPECT_EQ(c, 30);
}

// ────────────────────────────────────────────────
// Capacity Invariant
// ────────────────────────────────────────────────

TEST(RingBufferTest, enqueue_acceptsExactlyCapacityItems_whenStartingEmpty) {
  RingBuffer rb(8);

  for (int i = 0; i < 8; ++i) EXPECT_TRUE(rb.enqueue(i));
  EXPECT_FALSE(rb.enqueue(99));
}

// ────────────────────────────────────────────────
// Space Reclaim
// ────────────────────────────────────────────────

TEST(RingBufferTest, enqueue_succeeds_whenSpaceFreedByDequeue) {
  RingBuffer rb(4);
  for (int i = 1; i <= 4; ++i) ASSERT_TRUE(rb.enqueue(i));
  int discard;
  ASSERT_TRUE(rb.dequeue(&discard));  // slot 1 해제
  ASSERT_TRUE(rb.dequeue(&discard));  // slot 2 해제

  EXPECT_TRUE(rb.enqueue(50));
  EXPECT_TRUE(rb.enqueue(60));

  // 남은 순서: 3, 4, 50, 60
  int vals[4];
  for (int& v : vals) ASSERT_TRUE(rb.dequeue(&v));
  EXPECT_EQ(vals[0], 3);
  EXPECT_EQ(vals[1], 4);
  EXPECT_EQ(vals[2], 50);
  EXPECT_EQ(vals[3], 60);
}

// ────────────────────────────────────────────────
// Wrap-around
// ────────────────────────────────────────────────

TEST(RingBufferTest, enqueueDequeue_maintainsFIFOOrder_whenIndexWrapsAround) {
  // capacity=4로 4 round 반복 → 내부 index가 0..15를 순환
  RingBuffer rb(4);
  for (int round = 0; round < 4; ++round) {
    for (int i = 0; i < 4; ++i) ASSERT_TRUE(rb.enqueue(round * 4 + i + 1));
    for (int i = 0; i < 4; ++i) {
      int val;
      ASSERT_TRUE(rb.dequeue(&val));
      EXPECT_EQ(val, round * 4 + i + 1);
    }
  }
}

TEST(RingBufferTest, dequeue_returnsCorrectValues_whenManyWrapAroundsOccur) {
  // capacity=4로 1건씩 enqueue→dequeue 100회 반복 → index가 25번 wrap
  RingBuffer rb(4);
  for (int i = 1; i <= 100; ++i) {
    ASSERT_TRUE(rb.enqueue(i));
    int val;
    ASSERT_TRUE(rb.dequeue(&val));
    EXPECT_EQ(val, i);
  }
}

// ────────────────────────────────────────────────
// Boundary / Edge
// ────────────────────────────────────────────────

TEST(RingBufferTest, enqueueDequeue_worksCorrectly_whenCapacityIsOne) {
  // size=1: 2^0, 최소 유효 크기
  RingBuffer rb(1);

  EXPECT_TRUE(rb.enqueue(7));
  EXPECT_FALSE(rb.enqueue(8));  // full

  int val;
  EXPECT_TRUE(rb.dequeue(&val));
  EXPECT_EQ(val, 7);
  EXPECT_FALSE(rb.dequeue(&val));  // empty

  EXPECT_TRUE(rb.enqueue(9));  // slot reclaimed
}

TEST(RingBufferTest, enqueueDequeueCycle_preservesFIFO_overLargeIterations) {
  // 단일 스레드에서 10,000회 순차 enqueue/dequeue → 누락 없고 순서 보존
  RingBuffer rb(64);
  constexpr int kN = 10000;
  for (int i = 1; i <= kN; ++i) {
    ASSERT_TRUE(rb.enqueue(i));
    int val;
    ASSERT_TRUE(rb.dequeue(&val));
    EXPECT_EQ(val, i);
  }
}

// ────────────────────────────────────────────────
// SPSC Thread Safety
// ────────────────────────────────────────────────

TEST(RingBufferTest, enqueueDequeue_preservesAllItemsInFIFOOrder_whenSPSCConcurrent) {
  // Invariant: producer가 1..N을 순서대로 enqueue하면
  //            consumer는 1..N을 같은 순서로 dequeue해야 한다.
  RingBuffer rb(1024);
  constexpr int kN = 100000;

  std::vector<int> received;
  received.reserve(kN);
  // producer 완료 신호: consumer가 무한 spin하는 hang 방지
  std::atomic<bool> producer_done{false};

  std::thread producer([&]() {
    for (int i = 1; i <= kN; ++i) {
      while (!rb.enqueue(i)) {
        std::this_thread::yield();
      }
    }
    producer_done.store(true, std::memory_order_release);
  });

  std::thread consumer([&]() {
    int val;
    while (static_cast<int>(received.size()) < kN) {
      if (rb.dequeue(&val)) {
        received.push_back(val);
      } else if (producer_done.load(std::memory_order_acquire)) {
        // producer가 끝난 뒤 buffer가 비어있으면 아이템 손실 → hang 대신 실패
        std::this_thread::yield();
        if (!rb.dequeue(&val)) break;
        received.push_back(val);
      }
    }
  });

  producer.join();
  consumer.join();

  ASSERT_EQ(static_cast<int>(received.size()), kN);
  for (int i = 0; i < kN; ++i) {
    EXPECT_EQ(received[i], i + 1) << "FIFO violation at index " << i;
  }
}
