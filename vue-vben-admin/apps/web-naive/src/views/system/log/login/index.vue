<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';
import type { SysLoginLog } from '#/api';

import { h, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import {
  NButton,
  NCard,
  NDataTable,
  NInput,
  NPagination,
  NSelect,
  NSpace,
  NTag,
} from 'naive-ui';

import { dialog, message } from '#/adapter/naive';
import { cleanLoginLog, deleteLoginLog, getLoginLogPage } from '#/api';

defineOptions({ name: 'SystemLoginLog' });

const loading = ref(false);
const rows = ref<SysLoginLog[]>([]);
const total = ref(0);
const query = reactive({
  action: null as null | string,
  ip: '',
  pageNo: 1,
  pageSize: 10,
  status: null as null | string,
  username: '',
});
const statusOptions = [
  { label: '成功', value: 'true' },
  { label: '失败', value: 'false' },
];
const actionOptions = [
  { label: '登录', value: 'LOGIN' },
  { label: '退出', value: 'LOGOUT' },
];

const columns: DataTableColumns<SysLoginLog> = [
  { key: 'id', title: '日志编号', width: 100 },
  { key: 'username', minWidth: 140, title: '登录账号' },
  {
    key: 'action',
    render: (row) => (row.action === 'LOGIN' ? '登录' : '退出'),
    title: '认证动作',
    width: 100,
  },
  { key: 'ip', minWidth: 140, title: 'IP地址' },
  {
    ellipsis: { tooltip: true },
    key: 'userAgent',
    minWidth: 280,
    title: '客户端',
  },
  {
    key: 'status',
    render: (row) =>
      h(
        NTag,
        { bordered: false, type: row.status ? 'success' : 'error' },
        { default: () => (row.status ? '成功' : '失败') },
      ),
    title: '状态',
    width: 80,
  },
  {
    ellipsis: { tooltip: true },
    key: 'message',
    minWidth: 180,
    title: '结果消息',
  },
  { key: 'gmtCreated', minWidth: 170, title: '访问时间' },
  {
    fixed: 'right',
    key: 'actions',
    render: (row) =>
      h(
        NButton,
        {
          onClick: () => confirmDelete(row),
          quaternary: true,
          size: 'small',
          type: 'error',
        },
        { default: () => '删除' },
      ),
    title: '操作',
    width: 80,
  },
];

async function load() {
  loading.value = true;
  try {
    const result = await getLoginLogPage({
      ...query,
      action: query.action || undefined,
      status: query.status === null ? undefined : query.status === 'true',
    });
    rows.value = result.records;
    total.value = result.total;
  } finally {
    loading.value = false;
  }
}

function search() {
  query.pageNo = 1;
  load();
}

function reset() {
  Object.assign(query, {
    action: null,
    ip: '',
    pageNo: 1,
    status: null,
    username: '',
  });
  load();
}

function confirmDelete(row: SysLoginLog) {
  dialog.warning({
    content: `确定删除日志 #${row.id}？`,
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteLoginLog(row.id);
      message.success('登录日志已删除');
      await load();
    },
    positiveText: '删除',
    title: '删除登录日志',
  });
}

function confirmClean() {
  dialog.warning({
    content: '清空后无法恢复，确定清空全部登录日志？',
    negativeText: '取消',
    onPositiveClick: async () => {
      await cleanLoginLog();
      message.success('登录日志已清空');
      await load();
    },
    positiveText: '清空',
    title: '清空登录日志',
  });
}

onMounted(load);
</script>

<template>
  <Page description="查询登录成功、失败和退出记录" title="登录日志">
    <NCard :bordered="false" class="system-card">
      <div class="toolbar">
        <NSpace>
          <NInput
            v-model:value="query.username"
            clearable
            placeholder="登录账号"
            @keyup.enter="search"
          />
          <NInput
            v-model:value="query.ip"
            clearable
            placeholder="IP地址"
            @keyup.enter="search"
          />
          <NSelect
            v-model:value="query.action"
            :options="actionOptions"
            clearable
            placeholder="认证动作"
            style="width: 130px"
          />
          <NSelect
            v-model:value="query.status"
            :options="statusOptions"
            clearable
            placeholder="登录状态"
            style="width: 130px"
          />
          <NButton type="primary" @click="search">查询</NButton>
          <NButton @click="reset">重置</NButton>
        </NSpace>
        <NButton type="error" @click="confirmClean">清空日志</NButton>
      </div>
      <NDataTable
        :columns="columns"
        :data="rows"
        :loading="loading"
        :row-key="(row: SysLoginLog) => row.id"
        :scroll-x="1250"
      />
      <div class="pagination">
        <NPagination
          v-model:page="query.pageNo"
          v-model:page-size="query.pageSize"
          :item-count="total"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          @update:page="load"
          @update:page-size="
            query.pageNo = 1;
            load();
          "
        />
      </div>
    </NCard>
  </Page>
</template>

<style scoped>
.system-card {
  border-radius: 12px;
}
.toolbar {
  align-items: center;
  display: flex;
  gap: 16px;
  justify-content: space-between;
  margin-bottom: 16px;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
@media (max-width: 760px) {
  .toolbar {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
