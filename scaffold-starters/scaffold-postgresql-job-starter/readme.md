# PostgreSQL Job Queue

基于 PostgreSQL `FOR UPDATE SKIP LOCKED` 和 `LISTEN/NOTIFY` 的轻量任务队列，适合后台任务、异步事件和中低吞吐作业。

## 依赖

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-starter-postgresql-job</artifactId>
</dependency>
```

## 配置

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
        visibility-timeout: 5m
        idle-interval: 5s
        notify-poll-interval: 2s
        retry-delay: 30s
        exponential-backoff: true
```

## 投递任务

```java
@RestController
@RequiredArgsConstructor
class DemoController {

    private final PostgresqlJobQueue jobQueue;

    @PostMapping("/demo/jobs")
    public long createJob(@RequestBody DemoPayload payload) {
        return jobQueue.enqueue("demo", payload);
    }
}
```

## 消费任务

```java
@Component
class DemoJobHandler implements PostgresqlJobHandler {

    @Override
    public String queueName() {
        return "demo";
    }

    @Override
    public void handle(PostgresqlJob job) {
        // job.getPayload() 是 JSON 字符串，可用 ObjectMapper 转成业务对象。
    }
}
```

## 核心领取 SQL

```sql
WITH next_job AS (
    SELECT id
    FROM scaffold_jobs
    WHERE (status = 'pending' AND available_at <= CURRENT_TIMESTAMP)
       OR (status = 'processing' AND locked_until <= CURRENT_TIMESTAMP)
    ORDER BY priority DESC, created_at, id
    LIMIT 1
    FOR UPDATE SKIP LOCKED
)
UPDATE scaffold_jobs job
SET status = 'processing',
    attempts = job.attempts + 1,
    worker_id = ?,
    locked_at = CURRENT_TIMESTAMP,
    locked_until = CURRENT_TIMESTAMP + (? * INTERVAL '1 millisecond'),
    updated_at = CURRENT_TIMESTAMP
FROM next_job
WHERE job.id = next_job.id
RETURNING job.*;
```

边界：这个方案不替代 Kafka 的流处理、日志收集和百万级吞吐场景；它更适合已经使用 PostgreSQL 的应用内任务队列。
