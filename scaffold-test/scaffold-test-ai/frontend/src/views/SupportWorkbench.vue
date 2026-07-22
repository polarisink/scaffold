<!-- 阶段一至七统一售后工作台，负责组合各业务面板和共享选中工单。 -->
<script setup lang="ts">
import { useTemplateRef } from 'vue';

import ChatPanel from '@/components/ChatPanel.vue';
import IntentPanel from '@/components/IntentPanel.vue';
import ToolPanel from '@/components/ToolPanel.vue';
import WorkOrderList from '@/components/WorkOrderList.vue';
import KnowledgePanel from '@/components/KnowledgePanel.vue';
import SuggestionPanel from '@/components/SuggestionPanel.vue';
import RefundConfirmationPanel from '@/components/RefundConfirmationPanel.vue';
import type { WorkOrder } from '@/types/support';
import { useChatStore } from '@/stores/chat';

const intentPanel = useTemplateRef<InstanceType<typeof IntentPanel>>('intentPanel');
const workOrderList = useTemplateRef<InstanceType<typeof WorkOrderList>>('workOrderList');
const chat = useChatStore();

function onCreated(workOrder: WorkOrder) {
  workOrderList.value?.prepend(workOrder);
  chat.selectWorkOrder(workOrder);
}

function onSelect(workOrder: WorkOrder) {
  intentPanel.value?.showWorkOrder(workOrder);
  chat.selectWorkOrder(workOrder);
  window.scrollTo({ top: 520, behavior: 'smooth' });
}
</script>

<template>
  <div class="workbench">
    <div class="hero">
      <div>
        <a-typography-title :level="2">售后服务 AI 工作台</a-typography-title>
        <a-typography-paragraph>从结构化输出开始，逐步学习会话记忆、Tool Calling 与业务权限控制。</a-typography-paragraph>
      </div>
      <a-space wrap>
        <a-tag color="cyan">Spring AI</a-tag><a-tag color="green">Vue 3</a-tag><a-tag color="purple">JPA + PostgreSQL + pgvector</a-tag>
      </a-space>
    </div>
    <a-row :gutter="[20, 20]">
      <a-col :xs="24" :xl="16"><ChatPanel /></a-col>
      <a-col :xs="24" :xl="8"><ToolPanel /></a-col>
      <a-col :span="24"><KnowledgePanel /></a-col>
      <a-col :span="24"><SuggestionPanel /></a-col>
      <a-col :span="24"><RefundConfirmationPanel /></a-col>
      <a-col :span="24"><IntentPanel ref="intentPanel" @created="onCreated" /></a-col>
      <a-col :span="24"><WorkOrderList ref="workOrderList" @select="onSelect" /></a-col>
    </a-row>
  </div>
</template>
