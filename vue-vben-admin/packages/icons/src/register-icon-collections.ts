import { icons as lucideIcons } from '@iconify-json/lucide';
import { addCollection } from '@vben-core/icons';

// 菜单图标由后端以 `lucide:icon-name` 字符串下发。提前注册完整图标集，
// 避免 Iconify 在浏览器运行时请求公网 API，确保离线环境也能正常渲染。
addCollection(lucideIcons);
