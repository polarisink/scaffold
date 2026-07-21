<script setup lang="ts">
import { DeleteOutlined, SendOutlined } from '@ant-design/icons-vue';
import { nextTick, ref, useTemplateRef, watch } from 'vue';

import { useChatStore } from '@/stores/chat';

const chat = useChatStore();
const message = ref('');
const messageList = useTemplateRef<HTMLElement>('messageList');

watch(
  () => chat.messages.map((item) => item.content).join(''),
  async () => {
    await nextTick();
    if (messageList.value) messageList.value.scrollTop = messageList.value.scrollHeight;
  },
);

async function submit() {
  const content = message.value.trim();
  if (!content || chat.sending) return;
  message.value = '';
  await chat.send(content);
}

function onKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    void submit();
  }
}
</script>

<template>
  <a-card class="panel-card" :bordered="false">
    <template #title>
      <div class="panel-title"><span class="step">AI</span><span>工单多轮对话</span></div>
    </template>
    <template #extra>
      <a-button type="text" @click="chat.reset"><DeleteOutlined />清空显示</a-button>
    </template>

    <div ref="messageList" class="message-list">
      <div v-for="item in chat.messages" :key="item.id" class="message-row" :class="item.role">
        <div class="message-bubble">{{ item.content || '正在思考…' }}</div>
      </div>
    </div>
    <div class="composer">
      <a-textarea
        v-model:value="message"
        :auto-size="{ minRows: 2, maxRows: 5 }"
        :disabled="chat.sending || !chat.workOrderId || chat.closed"
        :placeholder="chat.closed ? '工单已关闭' : chat.workOrderId ? `继续咨询工单 #${chat.workOrderId}` : '请先创建或选择一个工单'"
        @keydown="onKeydown"
      />
      <a-button type="primary" :loading="chat.sending" :disabled="chat.closed" @click="submit"><SendOutlined />发送</a-button>
    </div>
  </a-card>
</template>
