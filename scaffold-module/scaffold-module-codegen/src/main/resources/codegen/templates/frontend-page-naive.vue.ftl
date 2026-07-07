<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { NButton, NCard, NDataTable, NSpace } from 'naive-ui';
import { list${className} } from '#/api/${moduleName}/${businessName}';
import type { ${className} } from '#/api/${moduleName}/${businessName}.model';

const loading = ref(false);
const rows = ref<${className}[]>([]);

const columns = [
<#list columns as column>
<#if column.listVisible>
  { key: '${column.propertyName}', title: '${column.columnComment!column.propertyName}' },
</#if>
</#list>
];

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
  <NCard title="${table.menuName}">
    <NSpace style="margin-bottom: 16px">
      <NButton type="primary">新增</NButton>
      <NButton :loading="loading" @click="load">刷新</NButton>
    </NSpace>
    <NDataTable :columns="columns" :data="rows" :loading="loading" />
  </NCard>
</template>
