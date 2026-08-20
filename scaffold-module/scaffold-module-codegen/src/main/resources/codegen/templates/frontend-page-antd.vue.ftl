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
    {
        dataIndex: '${column.propertyName}',
        key: '${column.propertyName}',
        title: '${column.columnComment!column.propertyName}',
    },
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

function changePage(pageNo: number, pageSize: number) {
    query.pageNo = pageSize === query.pageSize ? pageNo : 1;
    query.pageSize = pageSize;
    load();
}

onMounted(load);
</script>

<template>
    <ACard title="${table.menuName}">
        <ASpace style="margin-bottom: 16px">
            <AButton type="primary">新增</AButton>
            <AButton :loading="loading" @click="load">刷新</AButton>
        </ASpace>
        <ATable :columns="columns" :data-source="rows" :loading="loading" :pagination="false" row-key="id" />
        <APagination
            :current="query.pageNo"
            :page-size="query.pageSize"
            :page-size-options="['10', '20', '50']"
            :show-total="(count: number) => `共 ${r"${count}"} 条`"
            :total="total"
            show-size-changer
            style="margin-top: 16px; text-align: right"
            @change="changePage"
        />
    </ACard>
</template>
