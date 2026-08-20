<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';

import { page${className} } from '#/api/${moduleName}/${businessName}';
import type { ${className} } from '#/api/${moduleName}/${businessName}.model';

const loading = ref(false);
const rows = ref<${className}[]>([]);
const total = ref(0);
const query = reactive({ pageNo: 1, pageSize: 10 });

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
    <ElCard header="${table.menuName}">
        <div class="toolbar">
            <ElButton type="primary">新增</ElButton>
            <ElButton :loading="loading" @click="load">刷新</ElButton>
        </div>
        <ElTable v-loading="loading" :data="rows" border stripe>
            <#list columns as column>
                <#if column.listVisible>
                    <ElTableColumn prop="${column.propertyName}" label="${column.columnComment!column.propertyName}"
                                   min-width="140" />
                </#if>
            </#list>
        </ElTable>
        <ElPagination
            v-model:current-page="query.pageNo"
            v-model:page-size="query.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="load"
            @size-change="query.pageNo = 1; load()"
        />
    </ElCard>
</template>

<style scoped>
.toolbar {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;
}

.el-pagination {
    justify-content: flex-end;
    margin-top: 16px;
}
</style>
