package com.mimul.ratelimit;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.LongSupplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlidingWindowLog extends RateLimiter {
  private final LongSupplier clock;
  private final Queue<Long> windowLog = new LinkedList<>();

  protected SlidingWindowLog(int maxRequestPerSec) {
    this(maxRequestPerSec, System::currentTimeMillis);
  }

  SlidingWindowLog(int maxRequestPerSec, LongSupplier clock) {
    super(maxRequestPerSec);
    this.clock = clock;
  }

  @Override
  boolean allow() {
    long now = clock.getAsLong();
    long boundary = now - 1000;
    synchronized (windowLog) {
      while (!windowLog.isEmpty() && windowLog.element() <= boundary) {
        windowLog.poll();
      }
      windowLog.add(now);
      log.info("current time={}, log size ={}", now, windowLog.size());
      return windowLog.size() <= maxRequestPerSec;
    }
  }
}
