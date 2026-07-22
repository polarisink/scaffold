<!-- 阶段三工具面板：演示受权限控制的订单、物流和商品只读工具。 -->
<script setup lang="ts">
import { ApiOutlined, PlayCircleOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import { onMounted, reactive, ref } from 'vue';

import { errorMessage } from '@/api/http';
import { supportApi } from '@/api/support';
import type { AiTool } from '@/types/support';

const tools = ref<AiTool[]>([]);
const loading = ref(false);
const inputs = reactive<Record<string, string>>({});
const outputs = reactive<Record<string, string>>({});
const invoking = reactive<Record<string, boolean>>({});

async function loadTools() {
  loading.value = true;
  try {
    tools.value = await supportApi.listTools();
  } catch (error) {
    outputs._load = `工具加载失败：${errorMessage(error)}`;
  } finally {
    loading.value = false;
  }
}

async function invoke(tool: AiTool) {
  invoking[tool.name] = true;
  try {
    const input = inputs[tool.name]?.trim() ? JSON.parse(inputs[tool.name]) : {};
    const result = await supportApi.invokeTool(tool.name, input);
    outputs[tool.name] = result.result;
  } catch (error) {
    outputs[tool.name] = `错误：${errorMessage(error)}`;
  } finally {
    invoking[tool.name] = false;
  }
}

onMounted(loadTools);
</script>

<template>
  <a-card class="panel-card tool-panel" :bordered="false">
    <template #title>
      <div class="panel-title"><ApiOutlined /><span>已注册工具</span><a-badge :count="tools.length" /></div>
    </template>
    <template #extra>
      <a-button type="text" :loading="loading" @click="loadTools"><ReloadOutlined /></a-button>
    </template>
    <a-alert v-if="outputs._load" type="error" :message="outputs._load" show-icon />
    <a-empty v-else-if="!loading && !tools.length" description="暂无已注册工具" />
    <a-space v-else direction="vertical" :size="12" class="tool-list">
      <div v-for="tool in tools" :key="tool.name" class="tool-item">
        <strong>{{ tool.name }}</strong>
        <p>{{ tool.description || '无描述' }}</p>
        <a-input-group compact>
          <a-input v-model:value="inputs[tool.name]" style="width: calc(100% - 86px)" placeholder='JSON 参数，如 {"left":1}' />
          <a-button type="primary" :loading="invoking[tool.name]" @click="invoke(tool)"><PlayCircleOutlined />调用</a-button>
        </a-input-group>
        <pre v-if="outputs[tool.name]" class="tool-output">{{ outputs[tool.name] }}</pre>
      </div>
    </a-space>
  </a-card>
</template>
