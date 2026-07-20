import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

import { AUTH_TOKEN_KEY, jsonRequest, request } from '@/api/http';

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(AUTH_TOKEN_KEY) || '');
  const authenticated = computed(() => Boolean(token.value));

  async function login(username: string, password: string) {
    const value = await request<string>('/auth/login', jsonRequest('POST', { username, password }));
    token.value = value;
    localStorage.setItem(AUTH_TOKEN_KEY, value);
  }

  async function logout() {
    try {
      if (token.value) await request<void>('/auth/logout');
    } finally {
      clear();
    }
  }

  function clear() {
    token.value = '';
    localStorage.removeItem(AUTH_TOKEN_KEY);
  }

  return { authenticated, token, clear, login, logout };
});
