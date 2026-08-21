<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import { Profile } from '@vben/common-ui';
import { useUserStore } from '@vben/stores';

import ProfileBase from './base-setting.vue';
import ProfilePasswordSetting from './password-setting.vue';

const userStore = useUserStore();
const router = useRouter();

const tabsValue = ref<string>('basic');

const tabs = ref([
  {
    label: '账户信息',
    value: 'basic',
  },
  {
    label: '修改密码',
    value: 'password',
  },
]);

async function returnToAdmin() {
  await router.push(userStore.userInfo?.homePath || '/dashboard/analytics');
}
</script>
<template>
  <Profile
    v-model:model-value="tabsValue"
    title="个人中心"
    :user-info="userStore.userInfo"
    :tabs="tabs"
  >
    <template #content>
      <div class="relative size-full">
        <button
          class="absolute right-0 top-0 z-10 rounded-md bg-primary px-4 py-2 text-sm text-primary-foreground hover:opacity-90"
          type="button"
          @click="returnToAdmin"
        >
          返回主界面
        </button>
        <ProfileBase v-if="tabsValue === 'basic'" />
        <ProfilePasswordSetting v-if="tabsValue === 'password'" />
      </div>
    </template>
  </Profile>
</template>
