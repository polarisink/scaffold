<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';

import { page${className} } from '#/api/${moduleName}/${businessName}';
import type { ${className} } from '#/api/${moduleName}/${businessName}.model';

const loading = ref(false);
const rows = ref<${className}[]>([]);
const total = ref(0);
const query = reactive({ pageNo: 1, pageSize: 10 });

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
        const result = await page${className}(query);
        rows.value = result.records;
        total.value = result.total;
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
        <NPagination
            v-model:page="query.pageNo"
            v-model:page-size="query.pageSize"
            :item-count="total"
            :page-sizes="[10, 20, 50]"
            :prefix="({ itemCount }) => `共 ${r"${itemCount}"} 条`"
            show-size-picker
            style="margin-top: 16px; justify-content: flex-end"
            @update:page="load"
            @update:page-size="query.pageNo = 1; load()"
        />
    </NCard>
</template>
