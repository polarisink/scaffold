package com.scaffold.cloud.seata;

/**
 * Seata AT 分支写入结果。
 */
public record TransactionRecordResponse(
        String service,
        String businessKey,
        String xid,
        int affectedRows) {
}
