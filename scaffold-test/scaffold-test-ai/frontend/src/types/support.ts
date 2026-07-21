export type ChatRole = 'assistant' | 'error' | 'user';

export interface ChatMessage {
  id: string;
  content: string;
  role: ChatRole;
}

export interface PersistedMessage {
  id: number;
  sequence: number;
  role: 'USER' | 'ASSISTANT';
  content: string;
  createdAt: string;
}

export interface KnowledgeSource {
  documentId: string;
  title: string;
  version: string;
  updatedAt: string;
}

export interface KnowledgeAnswer {
  answer: string;
  sources: KnowledgeSource[];
  grounded: boolean;
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
