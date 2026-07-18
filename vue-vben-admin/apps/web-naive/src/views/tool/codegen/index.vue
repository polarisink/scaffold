<script setup lang="ts">
import type { DataTableColumns, DataTableRowKey } from 'naive-ui';

import type { CodegenConfig, DatabaseTable } from '#/api';

import { computed, h, onMounted, reactive, ref } from 'vue';

import { useAccess } from '@vben/access';
import { Page } from '@vben/common-ui';

import {
  NButton,
  NCard,
  NDataTable,
  NForm,
  NFormItem,
  NInput,
  NModal,
  NSelect,
  NSpace,
  NSwitch,
  NTabPane,
  NTabs,
  NTag,
} from 'naive-ui';

import { dialog, message } from '#/adapter/naive';
import {
  createCodegenConfig,
  deleteCodegenConfig,
  downloadCodegen,
  getCodegenConfig,
  getCodegenConfigs,
  getDatabaseTables,
  importDatabaseTable,
  updateCodegenConfig,
} from '#/api';

defineOptions({ name: 'CodeGenerator' });

const { hasAccessByCodes } = useAccess();
const activeSection = ref('configs');
const loading = ref(false);
const databaseLoading = ref(false);
const importing = ref(false);
const rows = ref<CodegenConfig[]>([]);
const databaseRows = ref<DatabaseTable[]>([]);
const configKeyword = ref('');
const databaseKeyword = ref('');
const selectedDatabaseKeys = ref<DataTableRowKey[]>([]);
const showEditor = ref(false);

const emptyConfig = (): CodegenConfig => ({
  author: 'scaffold',
  backendPath: 'scaffold-biz/src/main/java',
  businessName: '',
  className: '',
  columns: [],
  databaseType: 'mysql',
  frontendPath: 'vue-vben-admin/apps/web-naive/src',
  menuName: '',
  moduleName: 'system',
  packageName: 'com.scaffold.system',
  tableComment: '',
  tableName: '',
});

const editing = reactive<CodegenConfig>(emptyConfig());

const importedTableNames = computed(
  () => new Set(rows.value.map((row) => row.tableName)),
);

const filteredConfigs = computed(() => {
  const keyword = configKeyword.value.trim().toLowerCase();
  if (!keyword) return rows.value;

  return rows.value.filter((row) =>
    [
      row.tableName,
      row.className,
      row.moduleName,
      row.menuName,
      row.packageName,
    ].some((value) => value?.toLowerCase().includes(keyword)),
  );
});

const filteredDatabaseRows = computed(() => {
  const keyword = databaseKeyword.value.trim().toLowerCase();
  if (!keyword) return databaseRows.value;

  return databaseRows.value.filter(
    (row) =>
      row.name.toLowerCase().includes(keyword) ||
      row.comment?.toLowerCase().includes(keyword),
  );
});

const selectedDatabaseRows = computed(() =>
  databaseRows.value.filter((row) => selectedDatabaseKeys.value.includes(row.name)),
);

const configColumns: DataTableColumns<CodegenConfig> = [
  { key: 'tableName', minWidth: 160, title: '数据库表' },
  { key: 'className', minWidth: 150, title: 'Java 类' },
  { key: 'moduleName', title: '模块', width: 110 },
  { key: 'menuName', minWidth: 130, title: '菜单名称' },
  { key: 'packageName', minWidth: 210, title: '包名' },
  {
    key: 'capabilities',
    render: () =>
      h(NSpace, { size: 4 }, () => [
        h(NTag, { bordered: false, size: 'small', type: 'info' }, () => '后端'),
        h(NTag, { bordered: false, size: 'small', type: 'success' }, () => '前端'),
      ]),
    title: '生成内容',
    width: 150,
  },
  { key: 'gmtModified', title: '更新时间', width: 180 },
  {
    fixed: 'right',
    key: 'actions',
    render: (row) => {
      const actions: ReturnType<typeof h>[] = [];
      if (hasAccessByCodes(['tool:codegen:update'])) {
        actions.push(
          h(
            NButton,
            { onClick: () => edit(row), size: 'small', tertiary: true, type: 'primary' },
            () => '配置',
          ),
        );
      }
      if (hasAccessByCodes(['tool:codegen:download'])) {
        actions.push(
          h(
            NButton,
            { onClick: () => download(row), size: 'small', tertiary: true },
            () => '下载 ZIP',
          ),
        );
      }
      if (hasAccessByCodes(['tool:codegen:delete'])) {
        actions.push(
          h(
            NButton,
            { onClick: () => remove(row), quaternary: true, size: 'small', type: 'error' },
            () => '删除',
          ),
        );
      }
      return h(NSpace, { size: 4, wrap: false }, () => actions);
    },
    title: '操作',
    width: 230,
  },
];

const databaseColumns: DataTableColumns<DatabaseTable> = [
  {
    disabled: (row) => importedTableNames.value.has(row.name),
    type: 'selection',
  },
  { key: 'name', minWidth: 220, title: '数据库表' },
  {
    key: 'comment',
    minWidth: 260,
    render: (row) => row.comment || '-',
    title: '表注释',
  },
  { key: 'type', title: '类型', width: 120 },
  {
    key: 'status',
    render: (row) =>
      importedTableNames.value.has(row.name)
        ? h(NTag, { type: 'success' }, () => '已导入')
        : h(NTag, { bordered: false }, () => '待导入'),
    title: '状态',
    width: 120,
  },
  {
    key: 'action',
    render: (row) =>
      hasAccessByCodes(['tool:codegen:import'])
        ? h(
            NButton,
            {
              disabled: importedTableNames.value.has(row.name),
              onClick: () => importTable(row),
              size: 'small',
              type: 'primary',
            },
            () => (importedTableNames.value.has(row.name) ? '已导入' : '导入'),
          )
        : null,
    title: '操作',
    width: 120,
  },
];

async function load() {
  loading.value = true;
  try {
    rows.value = await getCodegenConfigs();
  } finally {
    loading.value = false;
  }
}

async function loadDatabase() {
  databaseLoading.value = true;
  try {
    databaseRows.value = await getDatabaseTables();
  } finally {
    databaseLoading.value = false;
  }
}

async function handleSectionChange(section: string) {
  activeSection.value = section;
  if (section === 'database' && databaseRows.value.length === 0) {
    await loadDatabase();
  }
}

function resetEditor(value = emptyConfig()) {
  Object.keys(editing).forEach((key) => delete (editing as any)[key]);
  Object.assign(editing, structuredClone(value));
}

function createConfig() {
  resetEditor();
  addColumn();
  showEditor.value = true;
}

async function edit(row: CodegenConfig) {
  resetEditor(await getCodegenConfig(row.id!));
  showEditor.value = true;
}

function addColumn() {
  editing.columns.push({
    autoIncrement: false,
    columnName: '',
    columnType: 'varchar',
    formVisible: true,
    formWidget: 'Input',
    javaType: 'String',
    jdbcType: 'VARCHAR',
    listVisible: true,
    nullable: true,
    primaryKey: false,
    propertyName: '',
    queryType: 'EQ',
    queryable: false,
    sortNo: editing.columns.length,
    tsType: 'string',
    uniqueKey: false,
  });
}

function syncNames() {
  const words = editing.tableName
    .replace(/^(biz|sys|t)_/, '')
    .split('_')
    .filter(Boolean);
  const className = words
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join('');

  editing.className ||= className;
  editing.businessName ||= words.join('-');
  editing.menuName ||= editing.tableComment || editing.className;
  editing.packageName ||= `com.scaffold.${editing.moduleName}`;
}

async function save() {
  syncNames();
  editing.columns.forEach((column, index) => {
    column.sortNo = index;
    column.propertyName ||= column.columnName
      .toLowerCase()
      .replace(/_([a-z0-9])/g, (_match, char: string) => char.toUpperCase());
    column.jdbcType = column.columnType.replace(/\(.*/, '').toUpperCase();
    column.uniqueKey = Boolean(column.uniqueKey) && !column.primaryKey;
    column.tsType =
      column.javaType === 'Boolean'
        ? 'boolean'
        : ['Integer', 'java.math.BigDecimal', 'Long'].includes(column.javaType)
          ? 'number'
          : 'string';
  });

  if (!editing.tableName || !editing.className || editing.columns.length === 0) {
    message.warning('请填写表名、类名并至少配置一个字段');
    return;
  }

  await (editing.id
    ? updateCodegenConfig(editing.id, editing)
    : createCodegenConfig(editing));
  message.success('生成配置已保存');
  showEditor.value = false;
  await load();
}

async function openImport() {
  await handleSectionChange('database');
}

async function importTable(row: DatabaseTable, openAfterImport = true) {
  const id = await importDatabaseTable(row.name, row.schema);
  message.success(`已导入 ${row.name}`);
  await load();
  if (openAfterImport) {
    activeSection.value = 'configs';
    await edit({ id } as CodegenConfig);
  }
}

async function bulkImport() {
  if (selectedDatabaseRows.value.length === 0) return;

  importing.value = true;
  let succeeded = 0;
  try {
    for (const row of selectedDatabaseRows.value) {
      await importDatabaseTable(row.name, row.schema);
      succeeded += 1;
    }
    selectedDatabaseKeys.value = [];
    await load();
    message.success(`成功导入 ${succeeded} 张表`);
  } finally {
    importing.value = false;
  }
}

async function download(row: CodegenConfig) {
  const blob = await downloadCodegen(row.id!);
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = `${row.businessName || row.tableName}-codegen.zip`;
  anchor.click();
  URL.revokeObjectURL(url);
}

function remove(row: CodegenConfig) {
  dialog.warning({
    content: `确定删除“${row.tableName}”的生成配置吗？`,
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteCodegenConfig(row.id!);
      await load();
    },
    positiveText: '删除',
    title: '删除生成配置',
  });
}

onMounted(load);
</script>

<template>
  <Page title="代码生成器" description="轻量配置元数据，生成并下载 CRUD 代码">
    <NCard :bordered="false">
      <NTabs
        :value="activeSection"
        animated
        type="line"
        @update:value="handleSectionChange"
      >
        <NTabPane name="configs" tab="生成配置">
          <div class="toolbar">
            <NSpace>
              <NButton
                v-access:code="'tool:codegen:create'"
                type="primary"
                @click="createConfig"
              >
                新建配置
              </NButton>
              <NButton v-access:code="'tool:codegen:import'" @click="openImport">
                从数据库导入
              </NButton>
              <NButton :loading="loading" @click="load">刷新</NButton>
            </NSpace>
            <NInput
              v-model:value="configKeyword"
              clearable
              placeholder="搜索表名、类名、模块"
              style="width: 280px"
            />
          </div>
          <NDataTable
            :columns="configColumns"
            :data="filteredConfigs"
            :loading="loading"
            :row-key="(row: CodegenConfig) => row.id || row.tableName"
            :scroll-x="1300"
          />
        </NTabPane>

        <NTabPane name="database" tab="数据库表">
          <div class="toolbar">
            <NSpace>
              <NButton
                v-access:code="'tool:codegen:import'"
                :disabled="selectedDatabaseRows.length === 0"
                :loading="importing"
                type="primary"
                @click="bulkImport"
              >
                批量导入（{{ selectedDatabaseRows.length }}）
              </NButton>
              <NButton :loading="databaseLoading" @click="loadDatabase">
                重新读取
              </NButton>
            </NSpace>
            <NInput
              v-model:value="databaseKeyword"
              clearable
              placeholder="搜索数据库表或注释"
              style="width: 320px"
            />
          </div>
          <NDataTable
            v-model:checked-row-keys="selectedDatabaseKeys"
            :columns="databaseColumns"
            :data="filteredDatabaseRows"
            :loading="databaseLoading"
            :row-key="(row: DatabaseTable) => row.name"
          />
        </NTabPane>
      </NTabs>
    </NCard>

    <NModal
      v-model:show="showEditor"
      preset="card"
      style="width: min(1280px, calc(100vw - 32px))"
      title="生成配置"
    >
      <NForm :model="editing" label-placement="top">
        <div class="form-grid">
          <NFormItem label="数据库表">
            <NInput v-model:value="editing.tableName" @blur="syncNames" />
          </NFormItem>
          <NFormItem label="表注释">
            <NInput v-model:value="editing.tableComment" />
          </NFormItem>
          <NFormItem label="Java 类名">
            <NInput v-model:value="editing.className" />
          </NFormItem>
          <NFormItem label="业务名">
            <NInput v-model:value="editing.businessName" />
          </NFormItem>
          <NFormItem label="模块">
            <NInput v-model:value="editing.moduleName" />
          </NFormItem>
          <NFormItem label="包名">
            <NInput v-model:value="editing.packageName" />
          </NFormItem>
          <NFormItem label="菜单名称">
            <NInput v-model:value="editing.menuName" />
          </NFormItem>
          <NFormItem label="后端源码目录">
            <NInput v-model:value="editing.backendPath" />
          </NFormItem>
          <NFormItem label="前端源码目录">
            <NInput v-model:value="editing.frontendPath" />
          </NFormItem>
        </div>
      </NForm>

      <div class="column-title">
        <b>字段配置（{{ editing.columns.length }}）</b>
        <NButton size="small" @click="addColumn">添加字段</NButton>
      </div>

      <div class="column-table-wrap">
        <table class="column-table">
          <thead>
            <tr>
              <th>列名</th>
              <th>属性</th>
              <th>注释</th>
              <th>SQL 类型</th>
              <th>Java 类型</th>
              <th>控件</th>
              <th>唯一</th>
              <th>查询</th>
              <th>查询方式</th>
              <th>列表</th>
              <th>表单</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(column, index) in editing.columns" :key="column.id || index">
              <td><NInput v-model:value="column.columnName" /></td>
              <td><NInput v-model:value="column.propertyName" /></td>
              <td><NInput v-model:value="column.columnComment" /></td>
              <td><NInput v-model:value="column.columnType" /></td>
              <td>
                <NSelect
                  v-model:value="column.javaType"
                  :options="
                    [
                      'String',
                      'Long',
                      'Integer',
                      'Boolean',
                      'java.math.BigDecimal',
                      'java.time.LocalDate',
                      'java.time.LocalDateTime',
                    ].map((value) => ({ label: value, value }))
                  "
                />
              </td>
              <td>
                <NSelect
                  v-model:value="column.formWidget"
                  :options="
                    [
                      'Input',
                      'Textarea',
                      'InputNumber',
                      'Switch',
                      'DatePicker',
                      'Select',
                    ].map((value) => ({ label: value, value }))
                  "
                />
              </td>
              <td><NSwitch v-model:value="column.uniqueKey" :disabled="column.primaryKey" /></td>
              <td><NSwitch v-model:value="column.queryable" /></td>
              <td>
                <NSelect
                  v-model:value="column.queryType"
                  :disabled="!column.queryable"
                  :options="[
                    { label: '精确', value: 'EQ' },
                    { label: '模糊', value: 'LIKE' },
                  ]"
                />
              </td>
              <td><NSwitch v-model:value="column.listVisible" /></td>
              <td><NSwitch v-model:value="column.formVisible" /></td>
              <td>
                <NButton text type="error" @click="editing.columns.splice(index, 1)">
                  移除
                </NButton>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <template #footer>
        <NSpace justify="end">
          <NButton @click="showEditor = false">取消</NButton>
          <NButton
            v-access:code="[
              editing.id ? 'tool:codegen:update' : 'tool:codegen:create',
            ]"
            type="primary"
            @click="save"
          >
            保存配置
          </NButton>
        </NSpace>
      </template>
    </NModal>
  </Page>
</template>

<style scoped>
.toolbar,
.column-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0 16px;
}

.column-table-wrap {
  overflow: auto;
}

.column-table {
  width: 100%;
  min-width: 1450px;
  border-collapse: collapse;
}

.column-table th,
.column-table td {
  padding: 7px;
  border: 1px solid var(--n-border-color);
  text-align: left;
}

.column-table th {
  background: var(--n-merged-th-color);
  white-space: nowrap;
}

@media (max-width: 1000px) {
  .form-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }
}

@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
