<script setup lang="ts">
import { RobotOutlined, SaveOutlined } from '@ant-design/icons-vue';
import { computed, ref } from 'vue';

import { errorMessage } from '@/api/http';
import { supportApi } from '@/api/support';
import { useChatStore } from '@/stores/chat';
import type { WorkOrder, WorkOrderIntent } from '@/types/support';

const emit = defineEmits<{ created: [workOrder: WorkOrder] }>();
const chat = useChatStore();
const description = ref('');
const result = ref<WorkOrderIntent>();
const analyzing = ref(false);
const creating = ref(false);
const status = ref('Prompt：support-intent@v1');
const failed = ref(false);
let requestId = crypto.randomUUID();

const fields = computed(() => [
  { label: '类别 category', value: result.value?.category || '—' },
  { label: '优先级 priority', value: result.value ? `${result.value.priority} / 5` : '—' },
  { label: '订单号 orderNo', value: result.value?.orderNo || '未提供' },
  { label: '人工审核', value: result.value ? (result.value.manualReviewRequired ? '需要' : '不需要') : '—' },
]);

function validate(): string | undefined {
  const value = description.value.trim();
  if (!value) {
    failed.value = true;
    status.value = '请输入售后问题描述';
    return;
  }
  failed.value = false;
  return value;
}

async function analyze() {
  const value = validate();
  if (!value) return;
  analyzing.value = true;
  status.value = '正在调用模型并转换为 WorkOrderIntent…';
  try {
    result.value = await supportApi.analyzeIntent(chat.conversationId, value);
    status.value = '分析完成 · Prompt：support-intent@v1';
  } catch (error) {
    failed.value = true;
    status.value = `分析失败：${errorMessage(error)}`;
  } finally {
    analyzing.value = false;
  }
}

async function create() {
  const value = validate();
  if (!value) return;
  creating.value = true;
  status.value = '正在分析并创建工单…';
  try {
    const workOrder = await supportApi.createWorkOrder(requestId, value);
    result.value = workOrder;
    status.value = `工单 #${workOrder.id} 创建成功 · ${workOrder.status}`;
    requestId = crypto.randomUUID();
    emit('created', workOrder);
  } catch (error) {
    failed.value = true;
    status.value = `创建失败：${errorMessage(error)}`;
  } finally {
    creating.value = false;
  }
}

function showWorkOrder(workOrder: WorkOrder) {
  description.value = workOrder.originalDescription;
  result.value = workOrder;
  failed.value = false;
  status.value = `工单 #${workOrder.id} · ${workOrder.status} · ${workOrder.createdAt}`;
}

defineExpose({ showWorkOrder });
</script>

<template>
  <a-card class="panel-card" :bordered="false">
    <template #title>
      <div class="panel-title"><span class="step">01</span><span>从描述中提取工单意图</span></div>
    </template>
    <a-row :gutter="[24, 20]">
      <a-col :xs="24" :lg="12">
        <a-typography-paragraph type="secondary">
          将自然语言转换为经过 Java 类型约束的 WorkOrderIntent。用户消息不能覆盖系统规则。
        </a-typography-paragraph>
        <a-textarea
          v-model:value="description"
          :maxlength="4000"
          :auto-size="{ minRows: 6, maxRows: 10 }"
          show-count
          placeholder="例如：我的手机无法开机，订单号202607190001，我想退款"
        />
        <a-space class="intent-actions">
          <a-button :loading="analyzing" @click="analyze"><RobotOutlined />仅分析意图</a-button>
          <a-button type="primary" :loading="creating" @click="create"><SaveOutlined />分析并创建工单</a-button>
        </a-space>
      </a-col>
      <a-col :xs="24" :lg="12">
        <div class="intent-grid">
          <div v-for="field in fields" :key="field.label" class="intent-field">
            <small>{{ field.label }}</small><strong>{{ field.value }}</strong>
          </div>
          <div class="intent-field wide"><small>摘要 summary</small><strong>{{ result?.summary || '等待分析' }}</strong></div>
        </div>
        <a-alert class="intent-status" :type="failed ? 'error' : 'info'" :message="status" show-icon />
      </a-col>
    </a-row>
  </a-card>
</template>
