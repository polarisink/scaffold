<script setup lang="ts">
import { computed } from 'vue';

import { useUserStore } from '@vben/stores';

const userStore = useUserStore();
const userInfo = computed(() => userStore.userInfo);
const roleNames = computed(() => userInfo.value?.roles?.join('、') || '无');
</script>
<template>
  <div class="max-w-3xl">
    <h2 class="mb-6 text-lg font-semibold">账户信息</h2>
    <dl class="divide-y rounded-lg border">
      <div class="grid grid-cols-[8rem_1fr] gap-4 px-5 py-4">
        <dt class="text-muted-foreground">用户名</dt>
        <dd>{{ userInfo?.username || '-' }}</dd>
      </div>
      <div class="grid grid-cols-[8rem_1fr] gap-4 px-5 py-4">
        <dt class="text-muted-foreground">所属组织</dt>
        <dd>{{ userInfo?.desc || '未分配' }}</dd>
      </div>
      <div class="grid grid-cols-[8rem_1fr] gap-4 px-5 py-4">
        <dt class="text-muted-foreground">当前角色</dt>
        <dd>{{ roleNames }}</dd>
      </div>
    </dl>
    <p class="mt-4 text-sm text-muted-foreground">
      账户角色和权限由系统管理员统一配置，个人中心仅提供查看功能。
    </p>
  </div>
</template>
