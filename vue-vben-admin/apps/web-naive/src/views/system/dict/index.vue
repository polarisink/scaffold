<script setup lang="ts">
import type {
  DictDataParams,
  DictTagType,
  DictTypeParams,
  SysDictData,
  SysDictType,
} from '#/api';
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui';

import { computed, h, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  NButton,
  NCard,
  NDataTable,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NModal,
  NPagination,
  NSelect,
  NSpace,
  NSwitch,
  NTag,
} from 'naive-ui';

import { dialog, message } from '#/adapter/naive';
import {
  createDictData,
  createDictType,
  deleteDictData,
  deleteDictType,
  getDictDataPage,
  getDictTypePage,
  updateDictData,
  updateDictType,
} from '#/api';

defineOptions({ name: 'SystemDict' });

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const saving = ref(false);
const rows = ref<SysDictType[]>([]);
const dataRows = ref<SysDictData[]>([]);
const dictTypeOptions = ref<Array<{ label: string; value: string }>>([]);
const total = ref(0);
const showTypeModal = ref(false);
const showDataModal = ref(false);
const typeFormRef = ref<FormInst | null>(null);
const dataFormRef = ref<FormInst | null>(null);
const editingTypeId = ref<number>();
const editingDataId = ref<number>();

const currentTypeCode = computed(() => {
  const value = route.query.dictType;
  return typeof value === 'string' ? value : '';
});
const dataView = computed(() => Boolean(currentTypeCode.value));
const currentTypeName = computed(() => {
  return (
    dictTypeOptions.value.find((item) => item.value === currentTypeCode.value)
      ?.label ||
    (typeof route.query.dictName === 'string' ? route.query.dictName : '') ||
    currentTypeCode.value
  );
});

const typeQuery = reactive({
  dictName: '',
  dictType: '',
  pageNo: 1,
  pageSize: 10,
  status: null as null | string,
});
const dataQuery = reactive({
  dictLabel: '',
  pageNo: 1,
  pageSize: 10,
  status: null as null | string,
});
const typeForm = reactive<DictTypeParams>({
  dictName: '',
  dictType: '',
  remark: '',
  status: true,
});
const dataForm = reactive<DictDataParams>({
  defaultFlag: false,
  dictLabel: '',
  dictSort: 0,
  dictType: '',
  dictValue: '',
  remark: '',
  status: true,
  tagType: 'default',
});
const typeRules: FormRules = {
  dictName: { message: '请输入字典名称', required: true, trigger: 'blur' },
  dictType: { message: '请输入字典类型', required: true, trigger: 'blur' },
};
const dataRules: FormRules = {
  dictLabel: { message: '请输入字典标签', required: true, trigger: 'blur' },
  dictValue: { message: '请输入字典值', required: true, trigger: 'blur' },
};
const statusOptions = [
  { label: '启用', value: 'true' },
  { label: '停用', value: 'false' },
];
const tagOptions: Array<{ label: string; value: DictTagType }> = [
  { label: '默认', value: 'default' },
  { label: '信息', value: 'info' },
  { label: '成功', value: 'success' },
  { label: '警告', value: 'warning' },
  { label: '错误', value: 'error' },
];

function statusTag(status: boolean) {
  return h(
    NTag,
    { bordered: false, type: status ? 'success' : 'default' },
    { default: () => (status ? '启用' : '停用') },
  );
}

const typeColumns: DataTableColumns<SysDictType> = [
  { key: 'id', title: '字典编号', width: 100 },
  { key: 'dictName', minWidth: 150, title: '字典名称' },
  { key: 'dictType', minWidth: 190, title: '字典类型' },
  {
    key: 'status',
    render: (row) => statusTag(row.status),
    title: '状态',
    width: 90,
  },
  { ellipsis: { tooltip: true }, key: 'remark', minWidth: 180, title: '备注' },
  { key: 'gmtCreated', minWidth: 170, title: '创建时间' },
  {
    fixed: 'right',
    key: 'actions',
    render: (row) =>
      h(NSpace, { size: 4 }, () => [
        h(
          NButton,
          {
            onClick: () => openTypeEdit(row),
            quaternary: true,
            size: 'small',
            type: 'primary',
          },
          { default: () => '编辑' },
        ),
        h(
          NButton,
          {
            onClick: () => openDataList(row),
            quaternary: true,
            size: 'small',
            type: 'info',
          },
          { default: () => '数据列表' },
        ),
        h(
          NButton,
          {
            onClick: () => confirmTypeDelete(row),
            quaternary: true,
            size: 'small',
            type: 'error',
          },
          { default: () => '删除' },
        ),
      ]),
    title: '操作',
    width: 210,
  },
];

const dataColumns: DataTableColumns<SysDictData> = [
  { key: 'id', title: '字典编码', width: 100 },
  { key: 'dictLabel', minWidth: 140, title: '字典标签' },
  { key: 'dictValue', minWidth: 140, title: '字典键值' },
  { key: 'dictSort', title: '字典排序', width: 100 },
  {
    key: 'tagType',
    render: (row) =>
      h(
        NTag,
        { bordered: false, type: row.tagType || 'default' },
        { default: () => row.dictLabel },
      ),
    title: '显示样式',
    width: 120,
  },
  {
    key: 'defaultFlag',
    render: (row) =>
      row.defaultFlag
        ? h(NTag, { bordered: false, type: 'info' }, { default: () => '默认' })
        : '-',
    title: '默认项',
    width: 90,
  },
  {
    key: 'status',
    render: (row) => statusTag(row.status),
    title: '状态',
    width: 90,
  },
  { ellipsis: { tooltip: true }, key: 'remark', minWidth: 160, title: '备注' },
  { key: 'gmtCreated', minWidth: 170, title: '创建时间' },
  {
    fixed: 'right',
    key: 'actions',
    render: (row) =>
      h(NSpace, { size: 4 }, () => [
        h(
          NButton,
          {
            onClick: () => openDataEdit(row),
            quaternary: true,
            size: 'small',
            type: 'primary',
          },
          { default: () => '编辑' },
        ),
        h(
          NButton,
          {
            onClick: () => confirmDataDelete(row),
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

async function loadTypes() {
  loading.value = true;
  try {
    const result = await getDictTypePage({
      ...typeQuery,
      status:
        typeQuery.status === null ? undefined : typeQuery.status === 'true',
    });
    rows.value = result.records;
    total.value = result.total;
  } finally {
    loading.value = false;
  }
}

async function loadTypeOptions() {
  const result = await getDictTypePage({ pageNo: 1, pageSize: 1000 });
  dictTypeOptions.value = result.records.map((item) => ({
    label: item.dictName,
    value: item.dictType,
  }));
}

async function loadData() {
  if (!currentTypeCode.value) return;
  loading.value = true;
  try {
    const result = await getDictDataPage({
      ...dataQuery,
      dictType: currentTypeCode.value,
      status:
        dataQuery.status === null ? undefined : dataQuery.status === 'true',
    });
    dataRows.value = result.records;
    total.value = result.total;
  } finally {
    loading.value = false;
  }
}

function searchTypes() {
  typeQuery.pageNo = 1;
  loadTypes();
}

function resetTypeQuery() {
  Object.assign(typeQuery, {
    dictName: '',
    dictType: '',
    pageNo: 1,
    status: null,
  });
  loadTypes();
}

function searchData() {
  dataQuery.pageNo = 1;
  loadData();
}

function resetDataQuery() {
  Object.assign(dataQuery, { dictLabel: '', pageNo: 1, status: null });
  loadData();
}

function openDataList(row: SysDictType) {
  router.push({
    path: route.path,
    query: { dictName: row.dictName, dictType: row.dictType },
  });
}

function changeDataType(dictType: string) {
  const dictName = dictTypeOptions.value.find(
    (item) => item.value === dictType,
  )?.label;
  router.replace({ path: route.path, query: { dictName, dictType } });
}

function backToTypes() {
  router.push({ path: route.path });
}

function openTypeCreate() {
  editingTypeId.value = undefined;
  Object.assign(typeForm, {
    dictName: '',
    dictType: '',
    remark: '',
    status: true,
  });
  showTypeModal.value = true;
}

function openTypeEdit(row: SysDictType) {
  editingTypeId.value = row.id;
  Object.assign(typeForm, {
    dictName: row.dictName,
    dictType: row.dictType,
    remark: row.remark || '',
    status: row.status,
  });
  showTypeModal.value = true;
}

async function submitType() {
  await typeFormRef.value?.validate();
  saving.value = true;
  try {
    await (editingTypeId.value
      ? updateDictType({ ...typeForm, id: editingTypeId.value })
      : createDictType(typeForm));
    message.success(`字典类型${editingTypeId.value ? '更新' : '创建'}成功`);
    showTypeModal.value = false;
    await loadTypes();
  } finally {
    saving.value = false;
  }
}

function confirmTypeDelete(row: SysDictType) {
  dialog.warning({
    content: `确定删除字典类型“${row.dictName}”？类型下存在数据时将无法删除。`,
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteDictType(row.id);
      message.success('字典类型已删除');
      await loadTypes();
    },
    positiveText: '删除',
    title: '删除字典类型',
  });
}

function openDataCreate() {
  editingDataId.value = undefined;
  Object.assign(dataForm, {
    defaultFlag: false,
    dictLabel: '',
    dictSort: 0,
    dictType: currentTypeCode.value,
    dictValue: '',
    remark: '',
    status: true,
    tagType: 'default',
  });
  showDataModal.value = true;
}

function openDataEdit(row: SysDictData) {
  editingDataId.value = row.id;
  Object.assign(dataForm, {
    defaultFlag: row.defaultFlag,
    dictLabel: row.dictLabel,
    dictSort: row.dictSort,
    dictType: row.dictType,
    dictValue: row.dictValue,
    remark: row.remark || '',
    status: row.status,
    tagType: row.tagType || 'default',
  });
  showDataModal.value = true;
}

async function submitData() {
  await dataFormRef.value?.validate();
  saving.value = true;
  try {
    await (editingDataId.value
      ? updateDictData({ ...dataForm, id: editingDataId.value })
      : createDictData(dataForm));
    message.success(`字典数据${editingDataId.value ? '更新' : '创建'}成功`);
    showDataModal.value = false;
    await loadData();
  } finally {
    saving.value = false;
  }
}

function confirmDataDelete(row: SysDictData) {
  dialog.warning({
    content: `删除字典项“${row.dictLabel}”后无法恢复，确定继续？`,
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteDictData(row.id);
      message.success('字典数据已删除');
      await loadData();
    },
    positiveText: '删除',
    title: '删除字典数据',
  });
}

watch(
  currentTypeCode,
  async (dictType) => {
    total.value = 0;
    if (dictType) {
      dataQuery.pageNo = 1;
      await loadTypeOptions();
      await loadData();
    } else {
      await loadTypes();
    }
  },
  { immediate: true },
);
</script>

<template>
  <Page
    :description="
      dataView
        ? `维护“${currentTypeName}”的字典选项`
        : '统一维护业务枚举、状态和下拉选项'
    "
    :title="dataView ? '字典数据' : '字典管理'"
  >
    <NCard v-if="!dataView" :bordered="false" class="system-card">
      <div class="toolbar">
        <NSpace>
          <NInput
            v-model:value="typeQuery.dictName"
            clearable
            placeholder="字典名称"
            @keyup.enter="searchTypes"
          />
          <NInput
            v-model:value="typeQuery.dictType"
            clearable
            placeholder="字典类型"
            @keyup.enter="searchTypes"
          />
          <NSelect
            v-model:value="typeQuery.status"
            :options="statusOptions"
            clearable
            placeholder="字典状态"
            style="width: 130px"
          />
          <NButton type="primary" @click="searchTypes">查询</NButton>
          <NButton @click="resetTypeQuery">重置</NButton>
        </NSpace>
        <NButton type="primary" @click="openTypeCreate">新增字典</NButton>
      </div>
      <NDataTable
        :columns="typeColumns"
        :data="rows"
        :loading="loading"
        :row-key="(row: SysDictType) => row.id"
        :scroll-x="1120"
      />
      <div class="pagination">
        <NPagination
          v-model:page="typeQuery.pageNo"
          v-model:page-size="typeQuery.pageSize"
          :item-count="total"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          @update:page="loadTypes"
          @update:page-size="
            typeQuery.pageNo = 1;
            loadTypes();
          "
        />
      </div>
    </NCard>

    <NCard v-else :bordered="false" class="system-card">
      <div class="toolbar">
        <NSpace>
          <NSelect
            :options="dictTypeOptions"
            :value="currentTypeCode"
            filterable
            placeholder="字典名称"
            style="width: 190px"
            @update:value="changeDataType"
          />
          <NInput
            v-model:value="dataQuery.dictLabel"
            clearable
            placeholder="字典标签"
            @keyup.enter="searchData"
          />
          <NSelect
            v-model:value="dataQuery.status"
            :options="statusOptions"
            clearable
            placeholder="数据状态"
            style="width: 130px"
          />
          <NButton type="primary" @click="searchData">查询</NButton>
          <NButton @click="resetDataQuery">重置</NButton>
        </NSpace>
        <NSpace>
          <NButton @click="backToTypes">返回字典管理</NButton>
          <NButton type="primary" @click="openDataCreate">新增数据</NButton>
        </NSpace>
      </div>
      <NDataTable
        :columns="dataColumns"
        :data="dataRows"
        :loading="loading"
        :row-key="(row: SysDictData) => row.id"
        :scroll-x="1250"
      />
      <div class="pagination">
        <NPagination
          v-model:page="dataQuery.pageNo"
          v-model:page-size="dataQuery.pageSize"
          :item-count="total"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          @update:page="loadData"
          @update:page-size="
            dataQuery.pageNo = 1;
            loadData();
          "
        />
      </div>
    </NCard>

    <NModal
      v-model:show="showTypeModal"
      preset="card"
      style="width: min(620px, calc(100vw - 32px))"
      :title="editingTypeId ? '编辑字典类型' : '新增字典类型'"
    >
      <NForm
        ref="typeFormRef"
        :model="typeForm"
        :rules="typeRules"
        label-placement="top"
      >
        <div class="form-grid">
          <NFormItem label="字典名称" path="dictName"
            ><NInput v-model:value="typeForm.dictName"
          /></NFormItem>
          <NFormItem label="字典类型" path="dictType"
            ><NInput
              v-model:value="typeForm.dictType"
              placeholder="例如：sys_user_status"
          /></NFormItem>
        </div>
        <NFormItem label="启用状态"
          ><NSwitch v-model:value="typeForm.status"
        /></NFormItem>
        <NFormItem label="备注"
          ><NInput v-model:value="typeForm.remark" type="textarea"
        /></NFormItem>
      </NForm>
      <template #footer
        ><NSpace justify="end"
          ><NButton @click="showTypeModal = false">取消</NButton
          ><NButton :loading="saving" type="primary" @click="submitType"
            >保存</NButton
          ></NSpace
        ></template
      >
    </NModal>

    <NModal
      v-model:show="showDataModal"
      preset="card"
      style="width: min(680px, calc(100vw - 32px))"
      :title="editingDataId ? '编辑字典数据' : '新增字典数据'"
    >
      <NForm
        ref="dataFormRef"
        :model="dataForm"
        :rules="dataRules"
        label-placement="top"
      >
        <div class="form-grid">
          <NFormItem label="字典标签" path="dictLabel"
            ><NInput v-model:value="dataForm.dictLabel"
          /></NFormItem>
          <NFormItem label="字典键值" path="dictValue"
            ><NInput v-model:value="dataForm.dictValue"
          /></NFormItem>
          <NFormItem label="显示样式"
            ><NSelect v-model:value="dataForm.tagType" :options="tagOptions"
          /></NFormItem>
          <NFormItem label="字典排序"
            ><NInputNumber
              v-model:value="dataForm.dictSort"
              :min="0"
              style="width: 100%"
          /></NFormItem>
        </div>
        <div class="form-grid">
          <NFormItem label="启用状态"
            ><NSwitch v-model:value="dataForm.status"
          /></NFormItem>
          <NFormItem label="默认选项"
            ><NSwitch v-model:value="dataForm.defaultFlag"
          /></NFormItem>
        </div>
        <NFormItem label="备注"
          ><NInput v-model:value="dataForm.remark" type="textarea"
        /></NFormItem>
      </NForm>
      <template #footer
        ><NSpace justify="end"
          ><NButton @click="showDataModal = false">取消</NButton
          ><NButton :loading="saving" type="primary" @click="submitData"
            >保存</NButton
          ></NSpace
        ></template
      >
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
.form-grid {
  display: grid;
  gap: 0 16px;
  grid-template-columns: 1fr 1fr;
}
@media (max-width: 900px) {
  .toolbar,
  .form-grid {
    align-items: stretch;
    display: flex;
    flex-direction: column;
  }
}
</style>
