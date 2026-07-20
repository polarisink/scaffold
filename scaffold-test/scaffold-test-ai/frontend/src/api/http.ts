import type { ApiEnvelope } from '@/types/support';

export const AUTH_TOKEN_KEY = 'scaffold-ai-support-token';

export function errorMessage(error: unknown): string {
  return error instanceof Error ? error.message : String(error);
}

export async function request<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await authFetch(url, init);
  const responseText = await response.text();
  let payload: ApiEnvelope<T> | T | undefined;
  if (responseText) {
    try {
      payload = JSON.parse(responseText) as ApiEnvelope<T> | T;
    } catch {
      throw new Error(response.ok ? '服务端返回格式不正确' : `请求失败（HTTP ${response.status}）`);
    }
  }
  if (!response.ok) {
    const message = typeof payload === 'object' && payload && 'message' in payload
      ? String(payload.message)
      : `请求失败（HTTP ${response.status}）`;
    throw new Error(message);
  }
  if (payload === undefined) return undefined as T;
  if (typeof payload === 'object' && payload && 'data' in payload && 'code' in payload) {
    if (payload.code !== 0) throw new Error(payload.message || `请求失败（${payload.code}）`);
    return payload.data;
  }
  return payload as T;
}

export async function authFetch(url: string, init?: RequestInit): Promise<Response> {
  const headers = new Headers(init?.headers);
  const token = localStorage.getItem(AUTH_TOKEN_KEY);
  if (token) headers.set('Authorization', `Bearer ${token}`);
  const response = await fetch(url, { ...init, headers });
  if (response.status === 401 && url !== '/auth/login') {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    window.dispatchEvent(new CustomEvent('auth:unauthorized'));
  }
  return response;
}

export function jsonRequest(method: string, body: unknown): RequestInit {
  return {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  };
}
