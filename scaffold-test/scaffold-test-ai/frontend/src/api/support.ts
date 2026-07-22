/** 集中声明阶段一至七售后业务接口，组件不直接拼接后端请求。 */
import { jsonRequest, request } from './http';
import type { AiTool, HandlingSuggestion, KnowledgeAnswer, PendingAction, PersistedMessage, RefundResult, WorkOrder, WorkOrderIntent } from '@/types/support';

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
  generateSuggestion(workOrderId: number) {
    return request<HandlingSuggestion>(
      `/api/examples/support/work-orders/${workOrderId}/suggestions`,
      jsonRequest('POST', {}),
    );
  },
  getLatestSuggestion(workOrderId: number) {
    return request<HandlingSuggestion>(
      `/api/examples/support/work-orders/${workOrderId}/suggestions/latest`,
    );
  },
  prepareRefund(orderNo: string, reason: string) {
    return request<PendingAction>(
      '/api/examples/support/refunds/prepare',
      jsonRequest('POST', { orderNo, reason }),
    );
  },
  confirmRefund(confirmationId: string) {
    return request<RefundResult>(
      '/api/examples/support/refunds/confirm',
      jsonRequest('POST', { confirmationId }),
    );
  },
  cancelRefund(confirmationId: string) {
    return request<PendingAction>(
      '/api/examples/support/refunds/cancel',
      jsonRequest('POST', { confirmationId }),
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
