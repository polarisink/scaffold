/** 阶段一至七前后端交互使用的售后领域类型。 */
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

export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type EvidenceType = 'WORK_ORDER' | 'ORDER' | 'KNOWLEDGE';

export interface HandlingEvidence {
  type: EvidenceType;
  reference: string;
  description: string;
}

export interface HandlingSuggestion {
  id: number;
  workOrderId: number;
  diagnosis: string;
  recommendedActions: string[];
  sources: KnowledgeSource[];
  evidence: HandlingEvidence[];
  riskLevel: RiskLevel;
  manualReviewRequired: boolean;
  generatedAt: string;
}

export type PendingActionStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED';

export interface PendingAction {
  confirmationId: string;
  userId: number;
  action: 'REFUND';
  orderNo: string;
  amount: number;
  reason: string;
  summary: string;
  expiresAt: string;
  status: PendingActionStatus;
}

export interface RefundResult {
  confirmationId: string;
  orderNo: string;
  amount: number;
  status: 'CONFIRMED';
  executedAt: string;
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
