package com.scaffold.support.suggestion;

/**
 * 可追溯的处理建议事实依据。
 */
public record HandlingEvidence(EvidenceType type, String reference, String description) {
}
