# scaffold-postgresql-job-starter

基于 PostgreSQL `FOR UPDATE SKIP LOCKED` 与 `LISTEN/NOTIFY` 的轻量任务队列，适合后台任务、异步事件和中低吞吐作业。

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-postgresql-job-starter</artifactId>
</dependency>
```

```yaml
scaffold:
  job:
    postgresql:
      enabled: true
      table-name: scaffold_jobs
      initialize-schema: true
      notify-channel: scaffold_job_created
      default-max-attempts: 3
      worker:
        enabled: true
        threads: 4
        queues: [email, report]
        visibility-timeout: 5m
        retry-delay: 30s
        exponential-backoff: true
```

```java
long id = jobQueue.enqueue("email", payload);

@Component
class EmailJobHandler implements PostgresqlJobHandler {
    public String queueName() { return "email"; }
    public void handle(PostgresqlJob job) { /* 处理 payload */ }
}
```

处理器应保证幂等，并让任务耗时小于 `visibility-timeout`。该模块不替代 Kafka 等流平台，不适用于日志流和百万级吞吐场景。
