package com.scaffold.support.refund;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 退款准备与用户二次确认的普通业务接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/examples/support/refunds")
@Tag(name = "退款二次确认", description = "退款准备不会执行退款，只有用户确认接口可以触发最终业务操作")
public class RefundController {

    private final RefundService service;

    @PostMapping("/prepare")
    @Operation(summary = "准备退款", description = "生成短期有效的一次性确认标识，不改变订单状态")
    public PendingAction prepare(@RequestBody @Valid PrepareRefundRequest request) {
        return service.prepare(request);
    }

    @PostMapping("/confirm")
    @Operation(summary = "确认并执行退款", description = "重新校验权限、金额和订单状态后幂等执行退款")
    public RefundResult confirm(@RequestBody @Valid ConfirmRefundRequest request) {
        return service.confirm(request.confirmationId());
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消退款确认")
    public PendingAction cancel(@RequestBody @Valid ConfirmRefundRequest request) {
        return service.cancel(request.confirmationId());
    }
}
