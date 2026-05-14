#include "ring_buffer.h"

#include <chrono>
#include <cstdint>
#include <iostream>
#include <thread>

constexpr uint64_t kBmtCount = 500000;

template <typename RingBufferType>
double Benchmark(RingBufferType& rb) {
  auto start = std::chrono::system_clock::now();
  std::thread workers[2] = {
      std::thread([&]() {
        for (uint64_t i = 0; i < kBmtCount; ++i) {
          int count = 1000;
          while (0 < count) {
            if (rb.enqueue(count)) {
              count--;
            }
          }
        }
      }),
      std::thread([&]() {
        int result;
        for (uint64_t i = 0; i < kBmtCount; ++i) {
          int count = 1000;
          while (0 < count) {
            if (rb.dequeue(&result)) {
              count--;
            }
          }
        }
      })};
  for (auto& w : workers) {
    w.join();
  }
  auto end = std::chrono::system_clock::now();
  double duration =
      std::chrono::duration_cast<std::chrono::nanoseconds>(end - start).count();
  const int count = kBmtCount * (1000 + 1000);
  std::cerr << count << " ops in " << duration << " ns \t";
  return 1000000.0 * kBmtCount * (1000 + 1000) / duration;
}

int main() {
  RingBuffer rb(2 * 1024 * 1024);
  std::cout << "RingBuffer: " << Benchmark(rb) << " ops/ms\n";
}
