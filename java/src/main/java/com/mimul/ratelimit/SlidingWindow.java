package com.mimul.ratelimit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

public class SlidingWindow extends RateLimiter {
  private final LongSupplier clock;
  private final ConcurrentMap<Long, AtomicInteger> windows = new ConcurrentHashMap<>();
  private final int windowSizeInMs;

  protected SlidingWindow(int maxRequestPerSec, int windowSizeInMs) {
    this(maxRequestPerSec, windowSizeInMs, System::currentTimeMillis);
  }

  SlidingWindow(int maxRequestPerSec, int windowSizeInMs, LongSupplier clock) {
    super(maxRequestPerSec);
    this.windowSizeInMs = windowSizeInMs;
    this.clock = clock;
  }

  @Override
  boolean allow() {
    long now = clock.getAsLong();
    long curWindowKey = now / windowSizeInMs;
    windows.putIfAbsent(curWindowKey, new AtomicInteger(0));
    long preWindowKey = curWindowKey - 1000;
    AtomicInteger preCount = windows.get(preWindowKey);
    if (preCount == null) {
      return windows.get(curWindowKey).incrementAndGet() <= maxRequestPerSec;
    }
    double preWeight = 1 - (now - curWindowKey) / 1000.0;
    long count = (long) (preCount.get() * preWeight + windows.get(curWindowKey).incrementAndGet());
    return count <= maxRequestPerSec;
  }
}
