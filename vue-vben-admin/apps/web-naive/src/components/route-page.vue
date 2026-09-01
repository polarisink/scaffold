<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';

defineOptions({ inheritAttrs: false });

const props = defineProps<{
  description?: string;
  title?: string;
}>();

const route = useRoute();
const resolvedTitle = computed(
  () => route.meta.title?.trim() || props.title || '',
);
const resolvedDescription = computed(
  () => route.meta.description?.trim() || props.description || '',
);
</script>

<template>
  <Page
    v-bind="$attrs"
    :description="resolvedDescription"
    :title="resolvedTitle"
  >
    <slot></slot>
  </Page>
</template>
