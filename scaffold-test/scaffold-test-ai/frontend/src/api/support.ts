import { jsonRequest, request } from './http';
import type { AiTool, KnowledgeAnswer, PersistedMessage, WorkOrder, WorkOrderIntent } from '@/types/support';

export const supportApi = {
  analyzeIntent(conversationId: string, message: string) {
    return request<WorkOrderIntent>(
      '/api/examples/support/intents/analyze',
      jsonRequest('POST', { conversationId, message }),
    );
  },
  createWorkOrder(requestId: string, description: string) {
    return request<WorkOrder>(
      '/api/examples/support/work-orders',
      jsonRequest('POST', { requestId, description }),
    );
  },
  listWorkOrders() {
    return request<WorkOrder[]>('/api/examples/support/work-orders');
  },
  getWorkOrder(id: number) {
    return request<WorkOrder>(`/api/examples/support/work-orders/${id}`);
  },
  chatWorkOrder(workOrderId: number, message: string) {
    return request<{ content: string }>(
      '/api/examples/support/assistant/chat',
      jsonRequest('POST', { workOrderId, message }),
    );
  },
  listMessages(workOrderId: number) {
    return request<PersistedMessage[]>(`/api/examples/support/work-orders/${workOrderId}/messages`);
  },
  answerKnowledge(workOrderId: number, question: string) {
    return request<KnowledgeAnswer>(
      '/api/examples/support/knowledge/answer',
      jsonRequest('POST', { workOrderId, question }),
    );
  },
  closeWorkOrder(id: number) {
    return request<WorkOrder>(
      `/api/examples/support/work-orders/${id}/close`,
      jsonRequest('POST', {}),
    );
  },
  listTools() {
    return request<AiTool[]>('/api/ai/tools');
  },
  invokeTool(name: string, input: Record<string, unknown>) {
    return request<{ result: string }>(
      `/api/ai/tools/${encodeURIComponent(name)}/invoke`,
      jsonRequest('POST', input),
    );
  },
};
