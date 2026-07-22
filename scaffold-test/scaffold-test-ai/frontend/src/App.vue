<!-- 应用根组件：展示统一品牌栏、认证状态和退出入口。 -->
<script setup lang="ts">
import { CustomerServiceOutlined, LogoutOutlined } from '@ant-design/icons-vue';
import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';

import { useAuthStore } from '@/stores/auth';

const route = useRoute();
const auth = useAuthStore();
const showHeader = computed(() => route.name !== 'login');
const loggingOut = ref(false);

async function logout() {
  if (loggingOut.value) return;
  loggingOut.value = true;
  try {
    await auth.logout();
  } finally {
    // 强制重新加载应用，避免下一位用户看到上一位用户留在 Pinia 中的工单和对话。
    window.location.replace('/login');
  }
}
</script>

<template>
  <a-layout class="app-shell">
    <a-layout-header v-if="showHeader" class="app-header">
      <div class="brand">
        <span class="brand-mark"><CustomerServiceOutlined /></span>
        <div>
          <strong>Scaffold AI Support</strong>
          <small>AI 智能售后工单学习系统</small>
        </div>
      </div>
      <a-space>
        <a-tag color="blue">RBAC 已认证</a-tag>
        <a-popconfirm title="确定退出当前账号并切换用户吗？" ok-text="退出" cancel-text="取消" @confirm="logout">
          <a-button class="logout-button" type="text" :loading="loggingOut"><LogoutOutlined />退出</a-button>
        </a-popconfirm>
      </a-space>
    </a-layout-header>
    <a-layout-content class="app-content">
      <router-view />
    </a-layout-content>
  </a-layout>
</template>
