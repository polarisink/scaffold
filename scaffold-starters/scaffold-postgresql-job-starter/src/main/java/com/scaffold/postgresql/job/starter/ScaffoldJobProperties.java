package com.scaffold.postgresql.job.starter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "scaffold.job.postgresql")
public class ScaffoldJobProperties {

    private boolean enabled = true;

    private String tableName = "scaffold_jobs";

    private boolean initializeSchema = true;

    private String notifyChannel = "scaffold_job_created";

    private int defaultMaxAttempts = 3;

    private Worker worker = new Worker();

    @Getter
    @Setter
    public static class Worker {

        private boolean enabled = true;

        private int threads = 4;

        private List<String> queues = new ArrayList<>();

        private Duration visibilityTimeout = Duration.ofMinutes(5);

        private Duration idleInterval = Duration.ofSeconds(5);

        private Duration notifyPollInterval = Duration.ofSeconds(2);

        private Duration retryDelay = Duration.ofSeconds(30);

        private boolean exponentialBackoff = true;
    }
}
