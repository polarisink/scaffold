<!-- 阶段二工单列表：创建、选择、刷新和关闭当前用户工单。 -->
<script setup lang="ts">
import { FileTextOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import { onMounted, ref } from 'vue';

import { errorMessage } from '@/api/http';
import { supportApi } from '@/api/support';
import type { WorkOrder } from '@/types/support';

const emit = defineEmits<{ select: [workOrder: WorkOrder] }>();
const workOrders = ref<WorkOrder[]>([]);
const loading = ref(false);
const error = ref('');

async function refresh() {
  loading.value = true;
  error.value = '';
  try {
    workOrders.value = await supportApi.listWorkOrders();
  } catch (cause) {
    error.value = `工单加载失败：${errorMessage(cause)}`;
  } finally {
    loading.value = false;
  }
}

async function select(id: number) {
  try {
    emit('select', await supportApi.getWorkOrder(id));
  } catch (cause) {
    error.value = `读取工单失败：${errorMessage(cause)}`;
  }
}

function prepend(workOrder: WorkOrder) {
  const index = workOrders.value.findIndex((item) => item.id === workOrder.id);
  if (index >= 0) workOrders.value.splice(index, 1);
  workOrders.value.unshift(workOrder);
}

defineExpose({ refresh, prepend });
onMounted(refresh);
</script>

<template>
  <a-card class="panel-card" :bordered="false">
    <template #title>
      <div class="panel-title"><span class="step">02</span><span>创建和管理售后工单</span></div>
    </template>
    <template #extra>
      <a-button type="text" :loading="loading" @click="refresh"><ReloadOutlined />刷新</a-button>
    </template>
    <a-alert v-if="error" type="error" :message="error" show-icon closable @close="error = ''" />
    <a-list :loading="loading" :data-source="workOrders" item-layout="horizontal">
      <template #renderItem="{ item }">
        <a-list-item class="work-order-item" @click="select(item.id)">
          <a-list-item-meta :description="`${item.category} · 优先级 ${item.priority} · ${item.orderNo || '无订单号'}`">
            <template #avatar><a-avatar shape="square"><FileTextOutlined /></a-avatar></template>
            <template #title><strong>#{{ item.id }} · {{ item.summary }}</strong></template>
          </a-list-item-meta>
          <a-tag color="processing">{{ item.status }}</a-tag>
        </a-list-item>
      </template>
      <template #empty><a-empty description="暂时没有工单" /></template>
    </a-list>
  </a-card>
</template>
