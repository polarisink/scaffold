<!-- 阶段五知识库面板：展示有依据回答、引用来源和无依据拒答。 -->
<script setup lang="ts">
import { ref } from 'vue';
import { supportApi } from '@/api/support';
import { errorMessage } from '@/api/http';
import { useChatStore } from '@/stores/chat';
import type { KnowledgeAnswer } from '@/types/support';

const chat = useChatStore();
const question = ref('');
const loading = ref(false);
const result = ref<KnowledgeAnswer>();

async function ask() {
  if (!chat.workOrderId || !question.value.trim()) return;
  loading.value = true;
  try {
    result.value = await supportApi.answerKnowledge(chat.workOrderId, question.value.trim());
  } catch (error) {
    result.value = { answer: `请求失败：${errorMessage(error)}`, sources: [], grounded: false };
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <a-card class="panel-card" :bordered="false" title="阶段五 · 售后知识库 RAG">
    <a-space direction="vertical" style="width: 100%">
      <a-input-search v-model:value="question" :disabled="!chat.workOrderId" :loading="loading"
        placeholder="例如：手机无法开机如何处理？" enter-button="检索并回答" @search="ask" />
      <a-alert v-if="result" :type="result.grounded ? 'success' : 'warning'" :message="result.answer" show-icon />
      <a-space v-if="result?.sources.length" wrap>
        <a-tag v-for="source in result.sources" :key="source.documentId" color="blue">
          {{ source.title }} · v{{ source.version }} · {{ source.documentId }}
        </a-tag>
      </a-space>
    </a-space>
  </a-card>
</template>
