package com.scaffold.support.conversation;

import com.scaffold.orm.BaseLongAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 工单对话消息实体，使用独立序号保证同一工单内的消息顺序。
 */
@Getter
@Setter
@Entity
@Table(name = "ai_support_message",
        uniqueConstraints = @UniqueConstraint(name = "uk_ai_support_message_sequence",
                columnNames = {"work_order_id", "message_sequence"}),
        indexes = @Index(name = "idx_ai_support_message_work_order", columnList = "work_order_id"))
public class SupportMessageEntity extends BaseLongAuditable {

    /**
     * 消息所属工单。
     */
    @Column(nullable = false)
    private Long workOrderId;

    /**
     * 工单内递增序号；既有列为 message_sequence，因此保留显式列映射。
     */
    @Column(name = "message_sequence", nullable = false)
    private Long sequence;

    /**
     * 消息角色，例如 USER 或 ASSISTANT。
     */
    @Column(nullable = false, length = 16)
    private String role;

    /**
     * 完整消息正文。
     */
    @Column(nullable = false, length = 8_000)
    private String content;
}
