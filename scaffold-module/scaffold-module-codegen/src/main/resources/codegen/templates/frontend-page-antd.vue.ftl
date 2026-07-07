<script setup lang="ts">
    import {onMounted, ref} from 'vue';
    import {list$

    {
        className
    }
    }
    from
    '#/api/';
    import type {${className}} from '#/api/${moduleName}/${businessName}.model';

    const loading = ref(false);
    const rows = ref < ${className}[] > ([]);

    const columns = [
        <#list columns as column>
        <#if column.listVisible>
        {
            dataIndex: '${column.propertyName}',
            key: '${column.propertyName}',
            title: '${column.columnComment!column.propertyName}'
        },
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
    <ACard title="${table.menuName}">
        <ASpace style="margin-bottom: 16px">
            <AButton type="primary">新增</AButton>
            <AButton :loading="loading" @click="load">刷新</AButton>
        </ASpace>
        <ATable :columns="columns" :data-source="rows" :loading="loading" row-key="id"/>
    </ACard>
</template>
