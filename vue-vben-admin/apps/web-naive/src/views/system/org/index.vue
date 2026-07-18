<script setup lang="ts">
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui';
import type { OrgParams, SysOrg } from '#/api';

import { computed, h, onMounted, reactive, ref } from 'vue';

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
  NSelect,
  NSpace,
} from 'naive-ui';

import { dialog, message } from '#/adapter/naive';
import { createOrg, deleteOrg, getOrgTree, updateOrg } from '#/api';
import { normalizeTreeIds, toNumberId } from '#/utils/id';

defineOptions({ name: 'SystemOrg' });

const loading = ref(false);
const saving = ref(false);
const showModal = ref(false);
const formRef = ref<FormInst | null>(null);
const rows = ref<SysOrg[]>([]);
const editingId = ref<number>();
const form = reactive<OrgParams>({
  orgCode: '',
  orgName: '',
  parentId: 0,
  sort: 0,
});
const rules: FormRules = {
  orgCode: { message: '请输入组织编码', required: true, trigger: 'blur' },
  orgName: { message: '请输入组织名称', required: true, trigger: 'blur' },
  parentId: {
    message: '请选择上级组织',
    required: true,
    trigger: 'change',
    type: 'number',
  },
};

function flattenTree(
  tree: SysOrg[],
  level = 0,
): Array<{ label: string; value: number }> {
  return tree.flatMap((item) => [
    { label: `${'　'.repeat(level)}${item.orgName}`, value: item.id },
    ...flattenTree(item.children || [], level + 1),
  ]);
}

function findNode(tree: SysOrg[], id?: number): SysOrg | undefined {
  for (const node of tree) {
    if (node.id === id) return node;
    const result = findNode(node.children || [], id);
    if (result) return result;
  }
}

function collectIds(node?: SysOrg): Set<number> {
  if (!node) return new Set();
  return new Set([
    node.id,
    ...(node.children || []).flatMap((child) => [...collectIds(child)]),
  ]);
}

const parentOptions = computed(() => {
  const unavailableIds = collectIds(findNode(rows.value, editingId.value));
  return [
    { label: '根组织', value: 0 },
    ...flattenTree(rows.value).filter(
      (option) => !unavailableIds.has(option.value),
    ),
  ];
});

const columns: DataTableColumns<SysOrg> = [
  { key: 'orgName', minWidth: 220, title: '组织名称', tree: true },
  { key: 'orgCode', minWidth: 180, title: '组织编码' },
  { key: 'sort', title: '排序', width: 90 },
  { key: 'gmtModified', minWidth: 170, title: '更新时间' },
  {
    fixed: 'right',
    key: 'actions',
    render: (row) =>
      h(NSpace, { size: 4 }, () => [
        h(
          NButton,
          {
            onClick: () => openCreate(row.id),
            quaternary: true,
            size: 'small',
          },
          { default: () => '新增下级' },
        ),
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
            onClick: () => confirmDelete(row),
            quaternary: true,
            size: 'small',
            type: 'error',
          },
          { default: () => '删除' },
        ),
      ]),
    title: '操作',
    width: 230,
  },
];

async function load() {
  loading.value = true;
  try {
    rows.value = normalizeTreeIds(await getOrgTree());
  } finally {
    loading.value = false;
  }
}

function resetForm(parentId = 0) {
  editingId.value = undefined;
  Object.assign(form, {
    orgCode: '',
    orgName: '',
    parentId: toNumberId(parentId) ?? 0,
    sort: 0,
  });
}

function openCreate(parentId = 0) {
  resetForm(parentId);
  showModal.value = true;
}

function openEdit(row: SysOrg) {
  editingId.value = row.id;
  Object.assign(form, {
    orgCode: row.orgCode,
    orgName: row.orgName,
    parentId: toNumberId(row.parentId) ?? 0,
    sort: row.sort || 0,
  });
  showModal.value = true;
}

async function submit() {
  await formRef.value?.validate();
  const payload = {
    ...form,
    parentId: toNumberId(form.parentId) ?? 0,
  };
  saving.value = true;
  try {
    await (editingId.value
      ? updateOrg({ ...payload, id: editingId.value })
      : createOrg(payload));
    message.success(`组织${editingId.value ? '更新' : '创建'}成功`);
    showModal.value = false;
    await load();
  } finally {
    saving.value = false;
  }
}

function confirmDelete(row: SysOrg) {
  dialog.warning({
    content: `删除组织“${row.orgName}”后无法恢复，确定继续？`,
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteOrg(row.id);
      message.success('组织已删除');
      await load();
    },
    positiveText: '删除',
    title: '删除组织',
  });
}

onMounted(load);
</script>

<template>
  <Page description="维护部门层级及用户的组织归属" title="组织管理">
    <NCard :bordered="false" class="system-card">
      <div class="toolbar">
        <div class="hint">组织采用树形结构；存在下级组织或用户时不能删除。</div>
        <NButton type="primary" @click="openCreate()">新增组织</NButton>
      </div>
      <NDataTable
        :columns="columns"
        :data="rows"
        default-expand-all
        :loading="loading"
        :row-key="(row: SysOrg) => row.id"
        :scroll-x="920"
      />
    </NCard>

    <NModal
      v-model:show="showModal"
      preset="card"
      style="width: min(640px, calc(100vw - 32px))"
      :title="editingId ? '编辑组织' : '新增组织'"
    >
      <NForm ref="formRef" :model="form" :rules="rules" label-placement="top">
        <NFormItem label="上级组织" path="parentId">
          <NSelect
            v-model:value="form.parentId"
            filterable
            :options="parentOptions"
          />
        </NFormItem>
        <div class="form-grid">
          <NFormItem label="组织名称" path="orgName">
            <NInput v-model:value="form.orgName" placeholder="例如：研发中心" />
          </NFormItem>
          <NFormItem label="组织编码" path="orgCode">
            <NInput v-model:value="form.orgCode" placeholder="例如：RND" />
          </NFormItem>
        </div>
        <NFormItem label="排序" path="sort">
          <NInputNumber
            v-model:value="form.sort"
            :min="0"
            style="width: 100%"
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
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}
.hint {
  color: hsl(var(--muted-foreground));
  font-size: 13px;
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
