package com.mimul.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

public class FixedWindowCounter extends RateLimiter {
  private final LongSupplier clock;
  private final ConcurrentMap<Long, AtomicInteger> windows = new ConcurrentHashMap<>();
  private final int windowSizeInMs;

  protected FixedWindowCounter(int maxRequestPerSec, int windowSizeInMs) {
    this(maxRequestPerSec, windowSizeInMs, System::currentTimeMillis);
  }

  FixedWindowCounter(int maxRequestPerSec, int windowSizeInMs, LongSupplier clock) {
    super(maxRequestPerSec);
    this.windowSizeInMs = windowSizeInMs;
    this.clock = clock;
  }

  @Override
  boolean allow() {
    long windowKey = clock.getAsLong() / windowSizeInMs;
    windows.putIfAbsent(windowKey, new AtomicInteger(0));
    return windows.get(windowKey).incrementAndGet() <= maxRequestPerSec;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("");
    for(Map.Entry<Long, AtomicInteger> entry:  windows.entrySet()) {
      sb.append(entry.getKey());
      sb.append(" --> ");
      sb.append(entry.getValue());
      sb.append("\n");
    }
    return sb.toString();
  }
}
