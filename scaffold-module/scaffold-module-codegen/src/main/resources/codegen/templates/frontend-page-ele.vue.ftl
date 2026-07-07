<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElButton, ElCard, ElTable, ElTableColumn } from 'element-plus';
import { list${className} } from '#/api/${moduleName}/${businessName}';
import type { ${className} } from '#/api/${moduleName}/${businessName}.model';

const loading = ref(false);
const rows = ref<${className}[]>([]);

async function load() {
  loading.value = true;
  try {
    rows.value = await list${className}();
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<template>
  <ElCard header="${table.menuName}">
    <div class="toolbar">
      <ElButton type="primary">新增</ElButton>
      <ElButton :loading="loading" @click="load">刷新</ElButton>
    </div>
    <ElTable v-loading="loading" :data="rows" border stripe>
<#list columns as column>
<#if column.listVisible>
      <ElTableColumn prop="${column.propertyName}" label="${column.columnComment!column.propertyName}" min-width="140" />
</#if>
</#list>
    </ElTable>
  </ElCard>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
</style>
