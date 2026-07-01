package com.scaffold.vo;

public record TransactionRecordCounts(
        String businessKey,
        long consumer,
        long provider,
        long order) {
}
