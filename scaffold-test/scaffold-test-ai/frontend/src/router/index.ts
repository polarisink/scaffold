/** 售后工作台路由配置，并在进入业务页面前校验本地登录状态。 */
import { createRouter, createWebHistory } from 'vue-router';

import { AUTH_TOKEN_KEY } from '@/api/http';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      name: 'support-workbench',
      component: () => import('@/views/SupportWorkbench.vue'),
    },
  ],
});

router.beforeEach((to) => {
  const authenticated = Boolean(localStorage.getItem(AUTH_TOKEN_KEY));
  if (!to.meta.public && !authenticated) return { name: 'login', query: { redirect: to.fullPath } };
  if (to.name === 'login' && authenticated) return { name: 'support-workbench' };
  return true;
});

window.addEventListener('auth:unauthorized', () => {
  if (router.currentRoute.value.name !== 'login') {
    void router.replace({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } });
  }
});

export default router;
