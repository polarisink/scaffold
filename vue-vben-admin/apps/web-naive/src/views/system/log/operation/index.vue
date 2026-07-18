<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';
import type { SysOperateLog } from '#/api';

import { h, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  NButton,
  NCard,
  NDataTable,
  NDescriptions,
  NDescriptionsItem,
  NInput,
  NModal,
  NPagination,
  NSelect,
  NSpace,
  NTag,
} from 'naive-ui';

import { dialog, message } from '#/adapter/naive';
import { cleanOperateLog, deleteOperateLog, getOperateLogPage } from '#/api';

defineOptions({ name: 'SystemOperateLog' });

const loading = ref(false);
const rows = ref<SysOperateLog[]>([]);
const total = ref(0);
const detail = ref<SysOperateLog>();
const showDetail = ref(false);
const query = reactive({
  operator: '',
  pageNo: 1,
  pageSize: 10,
  status: null as null | string,
  title: '',
});
const statusOptions = [
  { label: '成功', value: 'true' },
  { label: '失败', value: 'false' },
];

const columns: DataTableColumns<SysOperateLog> = [
  { key: 'id', title: '日志编号', width: 100 },
  { key: 'title', minWidth: 130, title: '模块' },
  { key: 'businessType', minWidth: 110, title: '操作类型' },
  { key: 'operator', minWidth: 120, title: '操作人' },
  { key: 'requestMethod', title: '请求方式', width: 100 },
  { ellipsis: { tooltip: true }, key: 'url', minWidth: 180, title: '请求地址' },
  { key: 'ip', minWidth: 130, title: 'IP地址' },
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
    key: 'costTime',
    render: (row) => `${row.costTime || 0} ms`,
    title: '耗时',
    width: 90,
  },
  { key: 'gmtCreated', minWidth: 170, title: '操作时间' },
  {
    fixed: 'right',
    key: 'actions',
    render: (row) =>
      h(NSpace, { size: 4 }, () => [
        h(
          NButton,
          {
            onClick: () => openDetail(row),
            quaternary: true,
            size: 'small',
            type: 'primary',
          },
          { default: () => '详情' },
        ),
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
      ]),
    title: '操作',
    width: 130,
  },
];

async function load() {
  loading.value = true;
  try {
    const result = await getOperateLogPage({
      ...query,
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
  Object.assign(query, { operator: '', pageNo: 1, status: null, title: '' });
  load();
}

function openDetail(row: SysOperateLog) {
  detail.value = row;
  showDetail.value = true;
}

function confirmDelete(row: SysOperateLog) {
  dialog.warning({
    content: `确定删除日志 #${row.id}？`,
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteOperateLog(row.id);
      message.success('操作日志已删除');
      await load();
    },
    positiveText: '删除',
    title: '删除操作日志',
  });
}

function confirmClean() {
  dialog.warning({
    content: '清空后无法恢复，确定清空全部操作日志？',
    negativeText: '取消',
    onPositiveClick: async () => {
      await cleanOperateLog();
      message.success('操作日志已清空');
      await load();
    },
    positiveText: '清空',
    title: '清空操作日志',
  });
}

onMounted(load);
</script>

<template>
  <Page description="查询系统业务操作、请求参数和执行结果" title="操作日志">
    <NCard :bordered="false" class="system-card">
      <div class="toolbar">
        <NSpace>
          <NInput
            v-model:value="query.title"
            clearable
            placeholder="模块名称"
            @keyup.enter="search"
          />
          <NInput
            v-model:value="query.operator"
            clearable
            placeholder="操作人"
            @keyup.enter="search"
          />
          <NSelect
            v-model:value="query.status"
            :options="statusOptions"
            clearable
            placeholder="操作状态"
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
        :row-key="(row: SysOperateLog) => row.id"
        :scroll-x="1450"
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

    <NModal
      v-model:show="showDetail"
      preset="card"
      style="width: min(850px, calc(100vw - 32px))"
      title="操作日志详情"
    >
      <NDescriptions v-if="detail" bordered :column="2" label-placement="left">
        <NDescriptionsItem label="模块">{{
          detail.title || '-'
        }}</NDescriptionsItem>
        <NDescriptionsItem label="操作类型">{{
          detail.businessType || '-'
        }}</NDescriptionsItem>
        <NDescriptionsItem label="操作人">{{
          detail.operator || '-'
        }}</NDescriptionsItem>
        <NDescriptionsItem label="客户端IP">{{
          detail.ip || '-'
        }}</NDescriptionsItem>
        <NDescriptionsItem label="请求地址" :span="2">{{
          detail.url || '-'
        }}</NDescriptionsItem>
        <NDescriptionsItem label="调用方法" :span="2">{{
          detail.method || '-'
        }}</NDescriptionsItem>
        <NDescriptionsItem label="操作内容" :span="2">{{
          detail.action || '-'
        }}</NDescriptionsItem>
        <NDescriptionsItem label="请求参数" :span="2">
          <pre>{{ detail.param || '-' }}</pre>
        </NDescriptionsItem>
        <NDescriptionsItem label="响应结果" :span="2">
          <pre>{{ detail.result || '-' }}</pre>
        </NDescriptionsItem>
        <NDescriptionsItem label="错误信息" :span="2">
          <pre>{{ detail.errorMsg || '-' }}</pre>
        </NDescriptionsItem>
      </NDescriptions>
    </NModal>
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
pre {
  margin: 0;
  max-height: 180px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
@media (max-width: 760px) {
  .toolbar {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
