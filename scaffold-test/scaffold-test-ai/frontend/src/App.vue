<script setup lang="ts">
import { CustomerServiceOutlined, LogoutOutlined } from '@ant-design/icons-vue';
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAuthStore } from '@/stores/auth';

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const showHeader = computed(() => route.name !== 'login');

async function logout() {
  await auth.logout();
  await router.replace({ name: 'login' });
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
        <a-button type="text" @click="logout"><LogoutOutlined />退出</a-button>
      </a-space>
    </a-layout-header>
    <a-layout-content class="app-content">
      <router-view />
    </a-layout-content>
  </a-layout>
</template>
