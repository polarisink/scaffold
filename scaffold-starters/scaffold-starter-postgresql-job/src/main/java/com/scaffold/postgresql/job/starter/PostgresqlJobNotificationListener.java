package com.scaffold.postgresql.job.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.TaskScheduler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@RequiredArgsConstructor
public class PostgresqlJobNotificationListener {

    private final ObjectProvider<DataSource> dataSourceProvider;
    private final TaskScheduler taskScheduler;
    private final PostgresqlJobStore jobStore;
    private final ScaffoldJobProperties properties;
    private final Runnable wakeup;

    private Connection listenerConnection;
    private PGConnection pgConnection;
    private ScheduledFuture<?> scheduledFuture;

    public void start() {
        if (!properties.isEnabled() || !properties.getWorker().isEnabled()) {
            return;
        }
        try {
            listenerConnection = requiredDataSource().getConnection();
            pgConnection = listenerConnection.unwrap(PGConnection.class);
            try (Statement statement = listenerConnection.createStatement()) {
                statement.execute("LISTEN " + jobStore.channel());
            }
            Duration interval = properties.getWorker().getNotifyPollInterval();
            if (interval == null || interval.isZero() || interval.isNegative()) {
                interval = Duration.ofSeconds(2);
            }
            scheduledFuture = taskScheduler.scheduleWithFixedDelay(this::pollNotifications, Instant.now().plus(interval), interval);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to start PostgreSQL job notification listener", ex);
        }
    }

    public void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        if (listenerConnection != null) {
            try {
                listenerConnection.close();
            } catch (SQLException ex) {
                log.warn("close PostgreSQL job notification listener failed: {}", ex.getMessage());
            }
        }
    }

    private void pollNotifications() {
        try (Statement statement = listenerConnection.createStatement()) {
            statement.execute("SELECT 1");
            PGNotification[] notifications = pgConnection.getNotifications();
            if (notifications == null || notifications.length == 0) {
                return;
            }
            wakeup.run();
        } catch (SQLException ex) {
            log.warn("poll PostgreSQL job notifications failed: {}", ex.getMessage());
        }
    }

    private DataSource requiredDataSource() {
        DataSource dataSource = dataSourceProvider.getIfAvailable();
        if (dataSource == null) {
            throw new IllegalStateException("PostgreSQL job notification listener requires a DataSource bean");
        }
        return dataSource;
    }
}
