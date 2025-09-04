package com.github.polarisink.download.scheduled;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class ClockTest {
    public static void main(String[] args) throws InterruptedException {
        AdjustableClock clock = new AdjustableClock(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));

        System.out.println("Initial: " + clock.instant());

        clock.step(Duration.ofSeconds(10));
        System.out.println("After 10s step: " + clock.instant());

        clock.setTime(Instant.parse("2025-01-01T12:00:00Z"));
        System.out.println("After setting new time: " + clock.instant());

        clock.resume();
        // Try to step while resumed — should throw exception
        try {
            clock.step(Duration.ofSeconds(5));
        } catch (Exception e) {
            System.out.println("Expected error: " + e.getMessage());
        }

        clock.pause();
        clock.step(Duration.ofMinutes(1));
        System.out.println("After 1 minute step: " + clock.instant());
    }
}
