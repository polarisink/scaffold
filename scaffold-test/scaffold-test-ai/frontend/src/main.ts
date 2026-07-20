import 'ant-design-vue/dist/reset.css';
import './styles/main.css';

import Antd from 'ant-design-vue';
import { createPinia } from 'pinia';
import { createApp } from 'vue';

import App from './App.vue';
import router from './router';

createApp(App).use(createPinia()).use(router).use(Antd).mount('#app');
