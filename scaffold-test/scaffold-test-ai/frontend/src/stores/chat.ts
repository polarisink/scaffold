import { defineStore } from 'pinia';
import { ref } from 'vue';

import { errorMessage } from '@/api/http';
import { supportApi } from '@/api/support';
import type { ChatMessage, WorkOrder } from '@/types/support';

const welcome = (): ChatMessage => ({
  id: crypto.randomUUID(),
  role: 'assistant',
  content: '你好，我是 Scaffold AI Support 演示助手。请描述你的售后问题。',
});

export const useChatStore = defineStore('chat', () => {
  const conversationId = ref(crypto.randomUUID());
  const workOrderId = ref<number>();
  const messages = ref<ChatMessage[]>([welcome()]);
  const sending = ref(false);

  function reset() {
    messages.value = [{ ...welcome(), content: workOrderId.value
      ? `已清空当前显示，可继续咨询工单 #${workOrderId.value}。`
      : '请先创建或选择一个工单。' }];
  }

  function selectWorkOrder(workOrder: WorkOrder) {
    workOrderId.value = workOrder.id;
    messages.value = [{ ...welcome(), content: `正在处理工单 #${workOrder.id}：${workOrder.summary}` }];
  }

  async function send(content: string) {
    if (!workOrderId.value) throw new Error('请先创建或选择一个工单');
    messages.value.push({ id: crypto.randomUUID(), role: 'user', content });
    const answer: ChatMessage = { id: crypto.randomUUID(), role: 'assistant', content: '' };
    messages.value.push(answer);
    sending.value = true;
    try {
      const response = await supportApi.chatWorkOrder(workOrderId.value, content);
      answer.content = response.content;
      if (!answer.content) throw new Error('模型未返回内容，请检查服务端日志与模型配置');
    } catch (error) {
      answer.role = 'error';
      answer.content = `请求失败：${errorMessage(error)}`;
    } finally {
      sending.value = false;
    }
  }

  return { conversationId, workOrderId, messages, sending, reset, selectWorkOrder, send };
});
