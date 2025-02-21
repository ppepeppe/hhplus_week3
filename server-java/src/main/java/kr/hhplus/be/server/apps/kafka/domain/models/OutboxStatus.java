package kr.hhplus.be.server.apps.kafka.domain.models;

public enum OutboxStatus {
    READY,
    SENT,
    FAILED
}
