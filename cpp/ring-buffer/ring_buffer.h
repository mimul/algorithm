#ifndef CPP_RING_BUFFER_RING_BUFFER_H_
#define CPP_RING_BUFFER_RING_BUFFER_H_

#include <atomic>
#include <cassert>
#include <cstddef>
#include <cstdint>
#include <stdexcept>
#include <vector>

class RingBuffer {
 public:
  explicit RingBuffer(size_t size) : buffer_(size) {
    if (size == 0 || (size & (size - 1)) != 0) {
      throw std::invalid_argument("RingBuffer: size must be a power of 2");
    }
  }

  // Returns true on success. Returns false if the buffer is full.
  bool enqueue(int item) {
    uint64_t write_idx = write_idx_.load(std::memory_order_relaxed);
    if (write_idx - cached_read_idx_ == buffer_.size()) {
      cached_read_idx_ = read_idx_.load(std::memory_order_acquire);
      if (write_idx - cached_read_idx_ == buffer_.size()) {
        return false;
      }
    }
    buffer_[write_idx & (buffer_.size() - 1)] = item;
    write_idx_.store(write_idx + 1, std::memory_order_release);
    return true;
  }

  // Returns true on success. Returns false if the buffer is empty.
  // Precondition: dest != nullptr. *dest is not modified on failure.
  bool dequeue(int* dest) {
    assert(dest != nullptr);
    uint64_t read_idx = read_idx_.load(std::memory_order_relaxed);
    if (cached_write_idx_ == read_idx) {
      cached_write_idx_ = write_idx_.load(std::memory_order_acquire);
      if (cached_write_idx_ == read_idx) {
        return false;
      }
    }
    *dest = buffer_[read_idx & (buffer_.size() - 1)];
    read_idx_.store(read_idx + 1, std::memory_order_release);
    return true;
  }

 private:
  std::vector<int> buffer_;
  alignas(64) std::atomic<uint64_t> read_idx_{0};
  alignas(64) uint64_t cached_read_idx_{0};
  alignas(64) std::atomic<uint64_t> write_idx_{0};
  alignas(64) uint64_t cached_write_idx_{0};
};

#endif  // CPP_RING_BUFFER_RING_BUFFER_H_
