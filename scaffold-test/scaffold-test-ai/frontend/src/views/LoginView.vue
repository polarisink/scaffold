<script setup lang="ts">
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue';
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { errorMessage } from '@/api/http';
import { useAuthStore } from '@/stores/auth';

const auth = useAuthStore();
const route = useRoute();
const router = useRouter();
const loading = ref(false);
const error = ref('');
const form = reactive({ username: 'admin', password: 'admin' });

async function submit() {
  error.value = '';
  loading.value = true;
  try {
    await auth.login(form.username.trim(), form.password);
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/';
    await router.replace(redirect);
  } catch (cause) {
    error.value = errorMessage(cause);
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="login-page">
    <a-card class="login-card" :bordered="false">
      <div class="login-heading">
        <div class="brand-mark"><UserOutlined /></div>
        <a-typography-title :level="3">登录 Scaffold AI Support</a-typography-title>
        <a-typography-paragraph type="secondary">登录身份由 RBAC 和 Spring Security 验证</a-typography-paragraph>
      </div>
      <a-alert v-if="error" class="login-error" type="error" :message="error" show-icon />
      <a-form :model="form" layout="vertical" @finish="submit">
        <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="form.username" size="large" autocomplete="username">
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model:value="form.password" size="large" autocomplete="current-password">
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>
        <a-button block size="large" type="primary" html-type="submit" :loading="loading">登录</a-button>
      </a-form>
      <a-alert class="demo-account" type="info" message="学习环境默认账号：admin / admin" show-icon />
    </a-card>
  </div>
</template>
