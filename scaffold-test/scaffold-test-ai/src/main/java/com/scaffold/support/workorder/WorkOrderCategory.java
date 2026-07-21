package com.scaffold.support.workorder;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "售后工单类别")
public enum WorkOrderCategory {
    ORDER_QUERY,
    LOGISTICS,
    REFUND,
    COMPLAINT,
    PRODUCT_CONSULTATION,
    REPAIR,
    UNKNOWN
}
