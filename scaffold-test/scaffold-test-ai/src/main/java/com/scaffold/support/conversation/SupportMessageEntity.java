package com.scaffold.support.conversation;

import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ai_support_message",
        uniqueConstraints = @UniqueConstraint(name = "uk_ai_support_message_sequence",
                columnNames = {"work_order_id", "message_sequence"}),
        indexes = @Index(name = "idx_ai_support_message_work_order", columnList = "work_order_id"))
public class SupportMessageEntity extends BaseAuditable {

    @Column(name = "work_order_id", nullable = false)
    private Long workOrderId;

    @Column(name = "message_sequence", nullable = false)
    private Long sequence;

    @Column(nullable = false, length = 16)
    private String role;

    @Column(nullable = false, length = 8_000)
    private String content;
}
