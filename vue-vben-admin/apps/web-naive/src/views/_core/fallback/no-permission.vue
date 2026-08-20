<script setup lang="ts">
import { Fallback } from '@vben/common-ui';

defineOptions({ name: 'NoPermissionPage' });
</script>

<template>
  <Fallback
    description="当前账号暂无可访问页面，请联系管理员分配权限"
    status="403"
    title="暂无权限"
  >
    <template #action></template>
  </Fallback>
</template>
