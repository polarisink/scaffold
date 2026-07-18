<script setup lang="ts">
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui';
import type { RoleParams, SysMenu, SysRole } from '#/api';

import { h, onMounted, reactive, ref } from 'vue';

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
  NTree,
} from 'naive-ui';

import { dialog, message } from '#/adapter/naive';
import {
  createRole,
  deleteRole,
  getMenuTree,
  getRoleDetail,
  getRolePage,
  updateRole,
} from '#/api';
import { normalizeTreeIds, toNumberId, toNumberIds } from '#/utils/id';

defineOptions({ name: 'SystemRole' });

const loading = ref(false);
const saving = ref(false);
const detailLoading = ref(false);
const showModal = ref(false);
const formRef = ref<FormInst | null>(null);
const rows = ref<SysRole[]>([]);
const menuTree = ref<SysMenu[]>([]);
const query = reactive({ pageNo: 1, pageSize: 10, roleCode: '', roleName: '' });
const total = ref(0);
const editingId = ref<number>();
const form = reactive<RoleParams>({
  description: '',
  menuIdList: [],
  roleCode: '',
  roleName: '',
});
const rules: FormRules = {
  roleCode: { message: '请输入角色编码', required: true, trigger: 'blur' },
  roleName: { message: '请输入角色名称', required: true, trigger: 'blur' },
};

const columns: DataTableColumns<SysRole> = [
  { key: 'roleName', minWidth: 140, title: '角色名称' },
  { key: 'roleCode', minWidth: 150, title: '角色编码' },
  { key: 'description', minWidth: 180, title: '描述' },
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
    const result = await getRolePage(query);
    rows.value = result.records.map((role) => ({
      ...role,
      id: toNumberId(role.id) ?? role.id,
    }));
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
  Object.assign(form, {
    description: '',
    menuIdList: [],
    roleCode: '',
    roleName: '',
  });
}

function openCreate() {
  resetForm();
  showModal.value = true;
}

async function openEdit(row: SysRole) {
  resetForm();
  editingId.value = row.id;
  Object.assign(form, {
    description: row.description || '',
    roleCode: row.roleCode,
    roleName: row.roleName,
  });
  showModal.value = true;
  detailLoading.value = true;
  try {
    const detail = await getRoleDetail(row.id);
    form.menuIdList = toNumberIds(detail.list);
  } finally {
    detailLoading.value = false;
  }
}

async function submit() {
  await formRef.value?.validate();
  const payload = {
    ...form,
    menuIdList: toNumberIds(form.menuIdList),
  };
  saving.value = true;
  try {
    await (editingId.value
      ? updateRole({ ...payload, id: editingId.value })
      : createRole(payload));
    message.success(`角色${editingId.value ? '更新' : '创建'}成功`);
    showModal.value = false;
    await load();
  } finally {
    saving.value = false;
  }
}

function confirmDelete(row: SysRole) {
  dialog.warning({
    content: `删除角色“${row.roleName}”后无法恢复，确定继续？`,
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteRole(row.id);
      message.success('角色已删除');
      await load();
    },
    positiveText: '删除',
    title: '删除角色',
  });
}

onMounted(async () => {
  await Promise.all([
    load(),
    getMenuTree().then((tree) => (menuTree.value = normalizeTreeIds(tree))),
  ]);
});
</script>

<template>
  <Page description="配置角色及其可访问的菜单与权限" title="角色管理">
    <NCard :bordered="false" class="system-card">
      <div class="toolbar">
        <NSpace>
          <NInput
            v-model:value="query.roleName"
            clearable
            placeholder="角色名称"
            @keyup.enter="search"
          />
          <NInput
            v-model:value="query.roleCode"
            clearable
            placeholder="角色编码"
            @keyup.enter="search"
          />
          <NButton type="primary" @click="search">查询</NButton>
          <NButton
            @click="
              query.roleName = '';
              query.roleCode = '';
              search();
            "
            >重置</NButton
          >
        </NSpace>
        <NButton type="primary" @click="openCreate">新增角色</NButton>
      </div>
      <NDataTable
        :columns="columns"
        :data="rows"
        :loading="loading"
        :row-key="(row: SysRole) => row.id"
        :scroll-x="850"
      />
      <div class="pagination">
        <NPagination
          v-model:page="query.pageNo"
          v-model:page-size="query.pageSize"
          :item-count="total"
          show-size-picker
          :page-sizes="[10, 20, 50]"
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
      style="width: min(720px, calc(100vw - 32px))"
      :title="editingId ? '编辑角色' : '新增角色'"
    >
      <NForm ref="formRef" :model="form" :rules="rules" label-placement="top">
        <div class="form-grid">
          <NFormItem label="角色名称" path="roleName">
            <NInput
              v-model:value="form.roleName"
              placeholder="例如：系统管理员"
            />
          </NFormItem>
          <NFormItem label="角色编码" path="roleCode">
            <NInput v-model:value="form.roleCode" placeholder="例如：admin" />
          </NFormItem>
        </div>
        <NFormItem label="描述" path="description">
          <NInput
            v-model:value="form.description"
            placeholder="角色职责说明"
            type="textarea"
          />
        </NFormItem>
        <NFormItem label="菜单权限" path="menuIdList">
          <div class="tree-panel">
            <NTree
              v-model:checked-keys="form.menuIdList"
              block-line
              cascade
              checkable
              :data="menuTree"
              :key-field="'id'"
              :label-field="'menuName'"
              :loading="detailLoading"
            />
          </div>
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
  gap: 16px;
}
.tree-panel {
  width: 100%;
  max-height: 320px;
  overflow: auto;
  padding: 12px;
  border: 1px solid var(--n-border-color);
  border-radius: 8px;
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
