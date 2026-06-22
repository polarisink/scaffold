package com.scaffold.postgresql.job.starter;

/**
 * 实现该接口即可消费指定队列。
 */
public interface PostgresqlJobHandler {

    String queueName();

    void handle(PostgresqlJob job) throws Exception;
}
