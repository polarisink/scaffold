<script setup lang="ts">
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui';
import type { ConfigParams, SysConfig } from '#/api';

import { computed, h, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  NButton,
  NCard,
  NDataTable,
  NForm,
  NFormItem,
  NInput,
  NModal,
  NPagination,
  NSpace,
  NSwitch,
  NTag,
} from 'naive-ui';

import { dialog, message } from '#/adapter/naive';
import { createConfig, deleteConfig, getConfigPage, updateConfig } from '#/api';

defineOptions({ name: 'SystemConfig' });

const loading = ref(false);
const saving = ref(false);
const showModal = ref(false);
const formRef = ref<FormInst | null>(null);
const rows = ref<SysConfig[]>([]);
const total = ref(0);
const editingId = ref<number>();
const originalSystemFlag = ref(false);
const query = reactive({
  configKey: '',
  configName: '',
  pageNo: 1,
  pageSize: 10,
});
const form = reactive<ConfigParams>({
  configKey: '',
  configName: '',
  configValue: '',
  remark: '',
  sysFlag: false,
});
const rules: FormRules = {
  configKey: { message: '请输入配置键', required: true, trigger: 'blur' },
  configName: { message: '请输入配置名称', required: true, trigger: 'blur' },
  configValue: { message: '请输入配置值', required: true, trigger: 'blur' },
};
const keyReadonly = computed(
  () => Boolean(editingId.value) && originalSystemFlag.value,
);

const columns: DataTableColumns<SysConfig> = [
  { key: 'configName', minWidth: 160, title: '配置名称' },
  { key: 'configKey', minWidth: 210, title: '配置键' },
  {
    ellipsis: { tooltip: true },
    key: 'configValue',
    minWidth: 220,
    title: '配置值',
  },
  {
    key: 'sysFlag',
    render: (row) =>
      h(
        NTag,
        { bordered: false, type: row.sysFlag ? 'warning' : 'default' },
        { default: () => (row.sysFlag ? '系统内置' : '普通配置') },
      ),
    title: '类型',
    width: 110,
  },
  {
    ellipsis: { tooltip: true },
    key: 'remark',
    minWidth: 180,
    title: '备注',
  },
  { key: 'gmtModified', minWidth: 170, title: '更新时间' },
  {
    fixed: 'right',
    key: 'actions',
    render: (row) =>
      h(NSpace, { size: 4 }, () => [
        h(
          NButton,
          {
            onClick: () => openEdit(row),
            quaternary: true,
            size: 'small',
            type: 'primary',
          },
          { default: () => '编辑' },
        ),
        h(
          NButton,
          {
            disabled: row.sysFlag,
            onClick: () => confirmDelete(row),
            quaternary: true,
            size: 'small',
            type: 'error',
          },
          { default: () => '删除' },
        ),
      ]),
    title: '操作',
    width: 140,
  },
];

async function load() {
  loading.value = true;
  try {
    const result = await getConfigPage(query);
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

function resetForm() {
  editingId.value = undefined;
  originalSystemFlag.value = false;
  Object.assign(form, {
    configKey: '',
    configName: '',
    configValue: '',
    remark: '',
    sysFlag: false,
  });
}

function openCreate() {
  resetForm();
  showModal.value = true;
}

function openEdit(row: SysConfig) {
  editingId.value = row.id;
  originalSystemFlag.value = row.sysFlag;
  Object.assign(form, {
    configKey: row.configKey,
    configName: row.configName,
    configValue: row.configValue,
    remark: row.remark || '',
    sysFlag: row.sysFlag,
  });
  showModal.value = true;
}

async function submit() {
  await formRef.value?.validate();
  saving.value = true;
  try {
    await (editingId.value
      ? updateConfig({ ...form, id: editingId.value })
      : createConfig(form));
    message.success(`配置${editingId.value ? '更新' : '创建'}成功`);
    showModal.value = false;
    await load();
  } finally {
    saving.value = false;
  }
}

function confirmDelete(row: SysConfig) {
  if (row.sysFlag) return;
  dialog.warning({
    content: `删除配置“${row.configName}”后无法恢复，确定继续？`,
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteConfig(row.id);
      message.success('配置已删除');
      await load();
    },
    positiveText: '删除',
    title: '删除配置',
  });
}

onMounted(load);
</script>

<template>
  <Page
    description="维护系统运行参数；系统内置配置不能删除或修改键名"
    title="系统配置"
  >
    <NCard :bordered="false" class="system-card">
      <div class="toolbar">
        <NSpace>
          <NInput
            v-model:value="query.configName"
            clearable
            placeholder="配置名称"
            @keyup.enter="search"
          />
          <NInput
            v-model:value="query.configKey"
            clearable
            placeholder="配置键"
            @keyup.enter="search"
          />
          <NButton type="primary" @click="search">查询</NButton>
          <NButton
            @click="
              query.configName = '';
              query.configKey = '';
              search();
            "
          >
            重置
          </NButton>
        </NSpace>
        <NButton type="primary" @click="openCreate">新增配置</NButton>
      </div>
      <NDataTable
        :columns="columns"
        :data="rows"
        :loading="loading"
        :row-key="(row: SysConfig) => row.id"
        :scroll-x="1240"
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
      v-model:show="showModal"
      preset="card"
      style="width: min(680px, calc(100vw - 32px))"
      :title="editingId ? '编辑配置' : '新增配置'"
    >
      <NForm ref="formRef" :model="form" :rules="rules" label-placement="top">
        <div class="form-grid">
          <NFormItem label="配置名称" path="configName">
            <NInput
              v-model:value="form.configName"
              placeholder="请输入配置名称"
            />
          </NFormItem>
          <NFormItem label="配置键" path="configKey">
            <NInput
              v-model:value="form.configKey"
              :disabled="keyReadonly"
              placeholder="例如：system.default-page-size"
            />
          </NFormItem>
        </div>
        <NFormItem label="配置值" path="configValue">
          <NInput
            v-model:value="form.configValue"
            :autosize="{ minRows: 2, maxRows: 6 }"
            placeholder="请输入配置值"
            type="textarea"
          />
        </NFormItem>
        <NFormItem label="系统内置" path="sysFlag">
          <NSwitch
            v-model:value="form.sysFlag"
            :disabled="originalSystemFlag"
          />
        </NFormItem>
        <NFormItem label="备注" path="remark">
          <NInput
            v-model:value="form.remark"
            :autosize="{ minRows: 2, maxRows: 5 }"
            placeholder="配置用途说明"
            type="textarea"
          />
        </NFormItem>
      </NForm>
      <template #footer>
        <NSpace justify="end">
          <NButton @click="showModal = false">取消</NButton>
          <NButton :loading="saving" type="primary" @click="submit">
            保存
          </NButton>
        </NSpace>
      </template>
    </NModal>
  </Page>
</template>

<style scoped>
.system-card {
  border-radius: 12px;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
}
@media (max-width: 640px) {
  .toolbar,
  .form-grid {
    align-items: stretch;
    display: flex;
    flex-direction: column;
  }
}
</style>
