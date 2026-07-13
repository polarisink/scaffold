package com.scaffold.postgresql.job.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.sql.DataSource;

@AutoConfiguration
@EnableConfigurationProperties(ScaffoldJobProperties.class)
public class PostgresqlJobAutoConfiguration {

    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    @ConditionalOnMissingBean
    public PostgresqlJobStore postgresqlJobStore(JdbcTemplate jdbcTemplate, ScaffoldJobProperties properties) {
        PostgresqlJobStore jobStore = new PostgresqlJobStore(jdbcTemplate, properties);
        if (properties.isEnabled() && properties.isInitializeSchema()) {
            jobStore.initializeSchema();
        }
        return jobStore;
    }

    @Bean
    @ConditionalOnBean(PostgresqlJobStore.class)
    @ConditionalOnMissingBean
    public PostgresqlJobQueue postgresqlJobQueue(PostgresqlJobStore jobStore,
                                                 ObjectProvider<ObjectMapper> objectMapperProvider,
                                                 ScaffoldJobProperties properties) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);
        return new PostgresqlJobQueue(jobStore, objectMapper, properties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "postgresqlJobTaskScheduler")
    public TaskScheduler postgresqlJobTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("postgresql-job-listener-");
        taskScheduler.setDaemon(true);
        return taskScheduler;
    }

    @Bean
    @ConditionalOnBean(PostgresqlJobStore.class)
    @ConditionalOnMissingBean
    public PostgresqlJobWorker postgresqlJobWorker(PostgresqlJobStore jobStore,
                                                   ScaffoldJobProperties properties,
                                                   ObjectProvider<PostgresqlJobHandler> handlers) {
        return new PostgresqlJobWorker(jobStore, properties, handlers.orderedStream().toList());
    }

    @Bean
    @ConditionalOnBean(PostgresqlJobWorker.class)
    public PostgresqlJobNotificationListener postgresqlJobNotificationListener(ObjectProvider<DataSource> dataSourceProvider,
                                                                               @Qualifier("postgresqlJobTaskScheduler")
                                                                               TaskScheduler taskScheduler,
                                                                               PostgresqlJobStore jobStore,
                                                                               ScaffoldJobProperties properties,
                                                                               PostgresqlJobWorker worker) {
        return new PostgresqlJobNotificationListener(dataSourceProvider, taskScheduler, jobStore, properties, worker::wakeup);
    }

    @Bean
    @ConditionalOnBean(PostgresqlJobWorker.class)
    public SmartLifecycle postgresqlJobLifecycle(PostgresqlJobWorker worker,
                                                 PostgresqlJobNotificationListener notificationListener) {
        return new SmartLifecycle() {
            private boolean running;

            @Override
            public void start() {
                worker.start();
                notificationListener.start();
                running = true;
            }

            @Override
            public void stop() {
                notificationListener.stop();
                worker.stop();
                running = false;
            }

            @Override
            public boolean isRunning() {
                return running;
            }
        };
    }
}
