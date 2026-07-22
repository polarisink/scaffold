<!-- 阶段六综合建议面板：展示诊断、操作建议、证据和风险等级。 -->
<script setup lang="ts">
import { computed, ref, watch } from 'vue';

import { errorMessage } from '@/api/http';
import { supportApi } from '@/api/support';
import { useChatStore } from '@/stores/chat';
import type { HandlingSuggestion, RiskLevel } from '@/types/support';

const chat = useChatStore();
const loading = ref(false);
const error = ref('');
const suggestion = ref<HandlingSuggestion>();

const riskColor: Record<RiskLevel, string> = {
  LOW: 'green',
  MEDIUM: 'orange',
  HIGH: 'red',
  CRITICAL: 'magenta',
};
const evidenceLabels: Record<string, string> = { WORK_ORDER: '工单', ORDER: '订单', KNOWLEDGE: '知识库' };
const canGenerate = computed(() => Boolean(chat.workOrderId) && !loading.value);

watch(() => chat.workOrderId, async (workOrderId) => {
  suggestion.value = undefined;
  error.value = '';
  if (!workOrderId) return;
  try {
    suggestion.value = await supportApi.getLatestSuggestion(workOrderId);
  } catch {
    // 尚未生成建议是正常状态，等待用户主动生成。
  }
}, { immediate: true });

async function generate() {
  if (!chat.workOrderId) return;
  loading.value = true;
  error.value = '';
  try {
    suggestion.value = await supportApi.generateSuggestion(chat.workOrderId);
  } catch (cause) {
    error.value = errorMessage(cause);
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <a-card class="panel-card" :bordered="false" title="阶段六 · 生成综合处理建议">
    <a-space direction="vertical" style="width: 100%" size="middle">
      <a-button type="primary" :disabled="!canGenerate" :loading="loading" @click="generate">
        生成处理建议
      </a-button>
      <a-alert v-if="!chat.workOrderId" message="请先创建或选择一个工单" type="info" show-icon />
      <a-alert v-if="error" :message="`生成失败：${error}`" type="error" show-icon />
      <template v-if="suggestion">
        <a-space wrap>
          <a-tag :color="riskColor[suggestion.riskLevel]">风险 {{ suggestion.riskLevel }}</a-tag>
          <a-tag :color="suggestion.manualReviewRequired ? 'red' : 'green'">
            {{ suggestion.manualReviewRequired ? '需要人工审核' : '无需人工审核' }}
          </a-tag>
        </a-space>
        <a-alert :message="suggestion.diagnosis" type="info" show-icon />
        <a-typography-title :level="5">推荐操作</a-typography-title>
        <a-steps direction="vertical" size="small" :current="-1"
          :items="suggestion.recommendedActions.map((title) => ({ title }))" />
        <a-typography-title :level="5">事实依据</a-typography-title>
        <a-list size="small" bordered :data-source="suggestion.evidence">
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta :description="item.description">
                <template #title>
                  <a-tag>{{ evidenceLabels[item.type] }}</a-tag>{{ item.reference }}
                </template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>
        <a-space v-if="suggestion.sources.length" wrap>
          <a-tag v-for="source in suggestion.sources" :key="source.documentId" color="blue">
            {{ source.title }} · v{{ source.version }}
          </a-tag>
        </a-space>
      </template>
    </a-space>
  </a-card>
</template>
