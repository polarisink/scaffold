<script setup lang="ts">
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui';
import type { SysOrg, SysRole, SysUser } from '#/api';

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
  NSelect,
  NSpace,
  NTag,
  NTreeSelect,
} from 'naive-ui';

import { dialog, message } from '#/adapter/naive';
import {
  createUser,
  deleteUser,
  getRolePage,
  getOrgTree,
  getUserDetail,
  getUserPage,
  resetUserPassword,
  toggleUserStatus,
  updateUser,
} from '#/api';
import { normalizeTreeIds, toNumberId, toNumberIds } from '#/utils/id';

defineOptions({ name: 'SystemUser' });

const loading = ref(false);
const saving = ref(false);
const showModal = ref(false);
const detailLoading = ref(false);
const formRef = ref<FormInst | null>(null);
const rows = ref<SysUser[]>([]);
const roles = ref<SysRole[]>([]);
const orgTree = ref<SysOrg[]>([]);
const query = reactive({ pageNo: 1, pageSize: 10, username: '' });
const total = ref(0);
const editingId = ref<number>();
const form = reactive<{
  orgId: null | number;
  password: string;
  roleIdList: number[];
  username: string;
}>({
  orgId: null,
  password: '',
  roleIdList: [] as number[],
  username: '',
});
const rules: FormRules = {
  orgId: {
    message: '请选择所属部门',
    required: true,
    trigger: 'change',
    type: 'number',
  },
  password: { message: '请输入初始密码', required: true, trigger: 'blur' },
  roleIdList: {
    message: '请至少选择一个角色',
    required: true,
    trigger: 'change',
    type: 'array',
  },
  username: { message: '请输入用户名', required: true, trigger: 'blur' },
};
const roleOptions = computed(() =>
  roles.value.map((role) => ({
    label: `${role.roleName}（${role.roleCode}）`,
    value: role.id,
  })),
);
const orgNameMap = computed(() => {
  const result = new Map<number, string>();
  const visit = (nodes: SysOrg[]) => {
    nodes.forEach((node) => {
      result.set(node.id, node.orgName);
      visit(node.children || []);
    });
  };
  visit(orgTree.value);
  return result;
});

function normalizeRole(role: SysRole): SysRole {
  return {
    ...role,
    id: toNumberId(role.id) ?? role.id,
  };
}

function normalizeUser(user: SysUser): SysUser {
  return {
    ...user,
    id: toNumberId(user.id) ?? user.id,
    orgId: toNumberId(user.orgId) ?? user.orgId,
  };
}

function confirmAction(options: {
  content: string;
  onPositiveClick: () => Promise<unknown>;
  title: string;
}) {
  dialog.warning({
    content: options.content,
    negativeText: '取消',
    onPositiveClick: async () => {
      await options.onPositiveClick();
      message.success('操作成功');
      await load();
    },
    positiveText: '确定',
    title: options.title,
  });
}

const columns: DataTableColumns<SysUser> = [
  { key: 'username', minWidth: 140, title: '用户名' },
  {
    key: 'orgId',
    minWidth: 160,
    render: (row) => orgNameMap.value.get(row.orgId) || `#${row.orgId}`,
    title: '所属部门',
  },
  {
    key: 'status',
    render: (row) =>
      h(
        NTag,
        { bordered: false, type: row.status ? 'success' : 'error' },
        { default: () => (row.status ? '正常' : '已禁用') },
      ),
    title: '状态',
    width: 100,
  },
  { key: 'gmtCreated', minWidth: 170, title: '创建时间' },
  {
    fixed: 'right',
    key: 'actions',
    render: (row) =>
      h(NSpace, { size: 2 }, () => [
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
            onClick: () =>
              confirmAction({
                content: `确定${row.status ? '禁用' : '启用'}用户“${row.username}”吗？`,
                onPositiveClick: () => toggleUserStatus(row.id),
                title: `${row.status ? '禁用' : '启用'}用户`,
              }),
            quaternary: true,
            size: 'small',
            type: row.status ? 'warning' : 'success',
          },
          { default: () => (row.status ? '禁用' : '启用') },
        ),
        h(
          NButton,
          {
            onClick: () =>
              confirmAction({
                content: `将用户“${row.username}”的密码恢复为系统默认值？`,
                onPositiveClick: () => resetUserPassword(row.id),
                title: '重置密码',
              }),
            quaternary: true,
            size: 'small',
          },
          { default: () => '重置密码' },
        ),
        h(
          NButton,
          {
            onClick: () =>
              confirmAction({
                content: `删除用户“${row.username}”后无法恢复，确定继续？`,
                onPositiveClick: () => deleteUser(row.id),
                title: '删除用户',
              }),
            quaternary: true,
            size: 'small',
            type: 'error',
          },
          { default: () => '删除' },
        ),
      ]),
    title: '操作',
    width: 290,
  },
];

async function load() {
  loading.value = true;
  try {
    const result = await getUserPage(query);
    rows.value = result.records.map(normalizeUser);
    total.value = result.total;
  } finally {
    loading.value = false;
  }
}

async function loadRoles() {
  const result = await getRolePage({ pageNo: 1, pageSize: 1000 });
  roles.value = result.records.map(normalizeRole);
}

async function loadOrgs() {
  orgTree.value = normalizeTreeIds(await getOrgTree());
}

function search() {
  query.pageNo = 1;
  load();
}

function openCreate() {
  editingId.value = undefined;
  Object.assign(form, {
    orgId: null,
    password: '',
    roleIdList: [],
    username: '',
  });
  showModal.value = true;
}

async function openEdit(row: SysUser) {
  editingId.value = row.id;
  Object.assign(form, {
    orgId: toNumberId(row.orgId),
    password: '',
    roleIdList: [],
    username: row.username,
  });
  showModal.value = true;
  detailLoading.value = true;
  try {
    const detail = await getUserDetail(row.id);
    Object.assign(form, {
      orgId: toNumberId(detail.user.orgId),
      roleIdList: toNumberIds(detail.roles.map((role) => role.id)),
      username: detail.user.username,
    });
  } finally {
    detailLoading.value = false;
  }
}

async function submit() {
  await formRef.value?.validate();
  const orgId = toNumberId(form.orgId);
  const roleIdList = toNumberIds(form.roleIdList);
  if (orgId === null) {
    return;
  }
  saving.value = true;
  try {
    await (editingId.value
      ? updateUser({
          id: editingId.value,
          orgId,
          roleIdList,
          username: form.username,
        })
      : createUser({
          orgId,
          password: form.password,
          roleIdList,
          username: form.username,
        }));
    message.success(`用户${editingId.value ? '更新' : '创建'}成功`);
    showModal.value = false;
    await load();
  } finally {
    saving.value = false;
  }
}

onMounted(async () => {
  await Promise.all([load(), loadRoles(), loadOrgs()]);
});
</script>

<template>
  <Page description="维护登录账号、所属部门、状态及角色归属" title="用户管理">
    <NCard :bordered="false" class="system-card">
      <div class="toolbar">
        <NSpace>
          <NInput
            v-model:value="query.username"
            clearable
            placeholder="搜索用户名"
            @keyup.enter="search"
          />
          <NButton type="primary" @click="search">查询</NButton>
          <NButton
            @click="
              query.username = '';
              search();
            "
            >重置</NButton
          >
        </NSpace>
        <NButton type="primary" @click="openCreate">新增用户</NButton>
      </div>
      <NDataTable
        :columns="columns"
        :data="rows"
        :loading="loading"
        :row-key="(row: SysUser) => row.id"
        :scroll-x="1040"
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
      style="width: min(620px, calc(100vw - 32px))"
      :title="editingId ? '编辑用户' : '新增用户'"
    >
      <NForm ref="formRef" :model="form" :rules="rules" label-placement="top">
        <NFormItem label="用户名" path="username">
          <NInput v-model:value="form.username" placeholder="用于登录系统" />
        </NFormItem>
        <NFormItem v-if="!editingId" label="初始密码" path="password">
          <NInput
            v-model:value="form.password"
            placeholder="请输入初始密码"
            show-password-on="click"
            type="password"
          />
        </NFormItem>
        <NFormItem label="所属部门" path="orgId">
          <NTreeSelect
            v-model:value="form.orgId"
            :options="orgTree"
            children-field="children"
            clearable
            filterable
            key-field="id"
            label-field="orgName"
            placeholder="请选择所属部门"
          />
        </NFormItem>
        <NFormItem label="所属角色" path="roleIdList">
          <NSelect
            v-model:value="form.roleIdList"
            filterable
            :loading="detailLoading"
            multiple
            :options="roleOptions"
            placeholder="请选择角色"
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
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
@media (max-width: 640px) {
  .toolbar {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
