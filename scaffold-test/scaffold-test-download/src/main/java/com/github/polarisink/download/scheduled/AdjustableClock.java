package com.github.polarisink.download.scheduled;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class AdjustableClock extends Clock {
    private final ZoneId zone;
    private Instant currentInstant;
    private boolean isPaused;

    public AdjustableClock(Instant initialInstant, ZoneId zone) {
        this.currentInstant = initialInstant;
        this.zone = zone;
        this.isPaused = true; // 默认是暂停的
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new AdjustableClock(this.currentInstant, zone);
    }

    @Override
    public Instant instant() {
        return currentInstant;
    }

    public void pause() {
        this.isPaused = true;
    }

    public void resume() {
        this.isPaused = false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void step(Duration stepBy) {
        if (isPaused) {
            currentInstant = currentInstant.plus(stepBy);
        } else {
            throw new IllegalStateException("Clock must be paused to step forward manually.");
        }
    }

    public void setTime(Instant newInstant) {
        this.currentInstant = newInstant;
    }
}
