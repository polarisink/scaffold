export type ChatRole = 'assistant' | 'error' | 'user';

export interface ChatMessage {
  id: string;
  content: string;
  role: ChatRole;
}

export interface AiTool {
  name: string;
  description?: string;
}

export interface WorkOrderIntent {
  category: string;
  summary: string;
  priority: number;
  orderNo: string | null;
  manualReviewRequired: boolean;
}

export interface WorkOrder extends WorkOrderIntent {
  id: number;
  userId: number;
  requestId: string;
  conversationId: string;
  status: string;
  originalDescription: string;
  createdAt: string;
}

export interface ApiEnvelope<T> {
  code: number;
  data: T;
  message?: string;
}
