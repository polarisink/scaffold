package com.scaffold.postgresql.job.starter;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class PostgresqlJob {

    private Long id;
    private String queueName;
    private PostgresqlJobStatus status;
    private String payload;
    private int attempts;
    private int maxAttempts;
    private int priority;
    private OffsetDateTime availableAt;
    private OffsetDateTime lockedAt;
    private OffsetDateTime lockedUntil;
    private String workerId;
    private String lastError;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime completedAt;
}
