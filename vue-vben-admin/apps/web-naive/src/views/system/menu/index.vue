<script setup lang="ts">
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui';
import type { MenuParams, SysMenu } from '#/api';

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
  NRadioButton,
  NRadioGroup,
  NSelect,
  NSpace,
  NTag,
} from 'naive-ui';

import { dialog, message } from '#/adapter/naive';
import { createMenu, deleteMenu, getMenuTree, updateMenu } from '#/api';
import { normalizeTreeIds, toNumberId } from '#/utils/id';

defineOptions({ name: 'SystemMenu' });

const loading = ref(false);
const saving = ref(false);
const showModal = ref(false);
const formRef = ref<FormInst | null>(null);
const rows = ref<SysMenu[]>([]);
const editingId = ref<number>();
const form = reactive<MenuParams>({
  menuIconUrl: '',
  menuName: '',
  menuType: 0,
  menuUrl: '',
  parentId: 0,
  path: '',
  sortNo: 0,
});
const rules: FormRules = {
  menuName: { message: '请输入菜单名称', required: true, trigger: 'blur' },
  menuType: {
    message: '请选择菜单类型',
    required: true,
    trigger: 'change',
    type: 'number',
  },
  parentId: {
    message: '请选择上级菜单',
    required: true,
    trigger: 'change',
    type: 'number',
  },
  path: { message: '请输入路由路径', required: true, trigger: 'blur' },
};

function flattenTree(
  tree: SysMenu[],
  level = 0,
): Array<{ label: string; value: number }> {
  return tree.flatMap((item) => [
    { label: `${'　'.repeat(level)}${item.menuName}`, value: item.id },
    ...flattenTree(item.children || [], level + 1),
  ]);
}

const parentOptions = computed(() => [
  { label: '根目录', value: 0 },
  ...flattenTree(rows.value).filter(
    (option) => option.value !== editingId.value,
  ),
]);

const columns: DataTableColumns<SysMenu> = [
  { key: 'menuName', minWidth: 180, title: '菜单名称', tree: true },
  { key: 'path', minWidth: 160, title: '路由路径' },
  {
    key: 'menuType',
    render: (row) =>
      h(
        NTag,
        { bordered: false, type: row.menuType === 0 ? 'info' : 'success' },
        { default: () => (row.menuType === 0 ? '目录' : '菜单') },
      ),
    title: '类型',
    width: 90,
  },
  { key: 'menuUrl', minWidth: 180, title: '权限标识 / URL' },
  { key: 'menuIconUrl', minWidth: 150, title: '图标' },
  { key: 'sortNo', title: '排序', width: 80 },
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
    width: 225,
  },
];

async function load() {
  loading.value = true;
  try {
    rows.value = normalizeTreeIds(await getMenuTree());
  } finally {
    loading.value = false;
  }
}

function resetForm(parentId = 0) {
  editingId.value = undefined;
  Object.assign(form, {
    menuIconUrl: '',
    menuName: '',
    menuType: 0,
    menuUrl: '',
    parentId: toNumberId(parentId) ?? 0,
    path: '',
    sortNo: 0,
  });
}

function openCreate(parentId = 0) {
  resetForm(parentId);
  showModal.value = true;
}

function openEdit(row: SysMenu) {
  editingId.value = row.id;
  Object.assign(form, {
    menuIconUrl: row.menuIconUrl || '',
    menuName: row.menuName,
    menuType: row.menuType,
    menuUrl: row.menuUrl || '',
    parentId: toNumberId(row.parentId) ?? 0,
    path: row.path,
    sortNo: row.sortNo || 0,
  });
  showModal.value = true;
}

async function submit() {
  await formRef.value?.validate();
  const parentId = toNumberId(form.parentId) ?? 0;
  saving.value = true;
  try {
    // 后端更新 DTO 当前将 parentId 定义为字符串，序列化为字符串兼容该接口。
    await (editingId.value
      ? updateMenu({
          ...form,
          id: editingId.value,
          parentId: String(parentId),
        })
      : createMenu({ ...form, parentId }));
    message.success(`菜单${editingId.value ? '更新' : '创建'}成功`);
    showModal.value = false;
    await load();
  } finally {
    saving.value = false;
  }
}

function confirmDelete(row: SysMenu) {
  dialog.warning({
    content: `删除“${row.menuName}”可能同时影响角色权限，确定继续？`,
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteMenu(row.id);
      message.success('菜单已删除');
      await load();
    },
    positiveText: '删除',
    title: '删除菜单',
  });
}

onMounted(load);
</script>

<template>
  <Page description="维护系统菜单树、路由和权限标识" title="菜单管理">
    <NCard :bordered="false" class="system-card">
      <div class="toolbar">
        <div class="hint">目录负责组织层级，菜单对应可访问页面或权限资源。</div>
        <NButton type="primary" @click="openCreate()">新增菜单</NButton>
      </div>
      <NDataTable
        :columns="columns"
        :data="rows"
        default-expand-all
        :loading="loading"
        :row-key="(row: SysMenu) => row.id"
        :scroll-x="1120"
      />
    </NCard>

    <NModal
      v-model:show="showModal"
      preset="card"
      style="width: min(700px, calc(100vw - 32px))"
      :title="editingId ? '编辑菜单' : '新增菜单'"
    >
      <NForm ref="formRef" :model="form" :rules="rules" label-placement="top">
        <div class="form-grid">
          <NFormItem label="菜单名称" path="menuName">
            <NInput
              v-model:value="form.menuName"
              placeholder="请输入菜单名称"
            />
          </NFormItem>
          <NFormItem label="上级菜单" path="parentId">
            <NSelect
              v-model:value="form.parentId"
              filterable
              :options="parentOptions"
            />
          </NFormItem>
          <NFormItem label="菜单类型" path="menuType">
            <NRadioGroup v-model:value="form.menuType">
              <NRadioButton :value="0">目录</NRadioButton>
              <NRadioButton :value="1">菜单</NRadioButton>
            </NRadioGroup>
          </NFormItem>
          <NFormItem label="排序" path="sortNo">
            <NInputNumber
              v-model:value="form.sortNo"
              :min="0"
              style="width: 100%"
            />
          </NFormItem>
          <NFormItem label="路由路径" path="path">
            <NInput
              v-model:value="form.path"
              placeholder="例如：/system/user"
            />
          </NFormItem>
          <NFormItem label="图标" path="menuIconUrl">
            <NInput
              v-model:value="form.menuIconUrl"
              placeholder="例如：lucide:users"
            />
          </NFormItem>
        </div>
        <NFormItem label="权限标识 / URL" path="menuUrl">
          <NInput
            v-model:value="form.menuUrl"
            placeholder="例如：system:user:list"
          />
        </NFormItem>
      </NForm>
      <template #footer>
        <NSpace justify="end">
          <NButton @click="showModal = false">取消</NButton>
          <NButton :loading="saving" type="primary" @click="submit"
            >保存</NButton
          >
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
  .form-grid {
    display: block;
  }
}
</style>
