<!-- 阶段七退款面板：准备待确认操作，并由用户明确确认或取消。 -->
<script setup lang="ts">
import { ref, watch } from 'vue';

import { errorMessage } from '@/api/http';
import { supportApi } from '@/api/support';
import { useChatStore } from '@/stores/chat';
import type { PendingAction, RefundResult } from '@/types/support';

const chat = useChatStore();
const orderNo = ref('');
const reason = ref('');
const loading = ref(false);
const error = ref('');
const pending = ref<PendingAction>();
const result = ref<RefundResult>();

watch(() => chat.workOrderId, async (workOrderId) => {
  pending.value = undefined;
  result.value = undefined;
  error.value = '';
  orderNo.value = '';
  reason.value = '';
  if (!workOrderId) return;
  try {
    const workOrder = await supportApi.getWorkOrder(workOrderId);
    orderNo.value = workOrder.orderNo ?? '';
    reason.value = workOrder.summary;
  } catch (cause) {
    error.value = errorMessage(cause);
  }
}, { immediate: true });

async function prepare() {
  if (!orderNo.value.trim() || !reason.value.trim()) return;
  loading.value = true;
  error.value = '';
  try {
    pending.value = await supportApi.prepareRefund(orderNo.value.trim(), reason.value.trim());
    result.value = undefined;
  } catch (cause) {
    error.value = errorMessage(cause);
  } finally {
    loading.value = false;
  }
}

async function confirm() {
  if (!pending.value) return;
  loading.value = true;
  error.value = '';
  try {
    result.value = await supportApi.confirmRefund(pending.value.confirmationId);
    pending.value.status = 'CONFIRMED';
  } catch (cause) {
    error.value = errorMessage(cause);
  } finally {
    loading.value = false;
  }
}

async function cancel() {
  if (!pending.value) return;
  loading.value = true;
  error.value = '';
  try {
    pending.value = await supportApi.cancelRefund(pending.value.confirmationId);
  } catch (cause) {
    error.value = errorMessage(cause);
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <a-card class="panel-card" :bordered="false" title="阶段七 · 退款准备与二次确认">
    <a-space direction="vertical" style="width: 100%" size="middle">
      <a-alert message="准备退款不会改变订单状态；只有点击确认后才会重新校验并执行。" type="warning" show-icon />
      <a-input v-model:value="orderNo" :disabled="!chat.workOrderId || loading || Boolean(pending)"
        placeholder="订单号" :maxlength="32" />
      <a-textarea v-model:value="reason" :disabled="!chat.workOrderId || loading || Boolean(pending)"
        placeholder="退款原因" :maxlength="500" show-count />
      <a-button type="primary" :disabled="!chat.workOrderId || !orderNo.trim() || !reason.trim() || Boolean(pending)"
        :loading="loading" @click="prepare">准备退款</a-button>
      <a-alert v-if="error" :message="error" type="error" show-icon />
      <a-descriptions v-if="pending" bordered size="small" :column="1" title="请核对退款信息">
        <a-descriptions-item label="订单号">{{ pending.orderNo }}</a-descriptions-item>
        <a-descriptions-item label="退款金额">¥ {{ pending.amount }}</a-descriptions-item>
        <a-descriptions-item label="退款原因">{{ pending.reason }}</a-descriptions-item>
        <a-descriptions-item label="有效期至">{{ new Date(pending.expiresAt).toLocaleString() }}</a-descriptions-item>
        <a-descriptions-item label="状态">{{ pending.status }}</a-descriptions-item>
      </a-descriptions>
      <a-space v-if="pending?.status === 'PENDING'">
        <a-popconfirm title="确认执行退款？该操作将改变订单售后状态。" ok-text="确认退款" cancel-text="返回"
          @confirm="confirm">
          <a-button type="primary" danger :loading="loading">二次确认退款</a-button>
        </a-popconfirm>
        <a-button :loading="loading" @click="cancel">取消</a-button>
      </a-space>
      <a-result v-if="result" status="success" title="退款已确认执行"
        :sub-title="`订单 ${result.orderNo}，金额 ¥${result.amount}`" />
    </a-space>
  </a-card>
</template>
