package com.scaffold.support.suggestion;

/**
 * 综合处理建议的风险等级。
 */
public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    /**
     * 返回两个风险等级中较高的一个。
     */
    public static RiskLevel max(RiskLevel left, RiskLevel right) {
        return left.ordinal() >= right.ordinal() ? left : right;
    }
}
