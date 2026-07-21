package com.scaffold.support.workorder;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "售后工单生命周期状态，由Java业务逻辑控制")
public enum WorkOrderStatus {
    OPEN,
    MANUAL_REVIEW,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}
