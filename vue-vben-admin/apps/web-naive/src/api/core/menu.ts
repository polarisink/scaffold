import type { RouteRecordStringComponent } from '@vben/types';

import type { RbacMenu } from './user';

import { getCachedUserContext } from './user';

/**
 * 已知核心页面使用显式注册；代码生成页面按规范映射到
 * views/<route>/index.vue，实际组件仍受 Vite import.meta.glob 编译清单约束。
 */
const PAGE_COMPONENT_REGISTRY: Record<string, string> = {
  '/dashboard/analytics': '/dashboard/analytics/index',
  '/dashboard/workspace': '/dashboard/workspace/index',
  '/system/menu': '/system/menu/index',
  '/system/org': '/system/org/index',
  '/system/config': '/system/config/index',
  '/system/dict': '/system/dict/index',
  '/system/log/login': '/system/log/login/index',
  '/system/log/operation': '/system/log/operation/index',
  '/system/role': '/system/role/index',
  '/system/user': '/system/user/index',
  '/tool/codegen': '/tool/codegen/index',
};

const FALLBACK_COMPONENT = '/_core/fallback/not-found';

function normalizeFullPath(path: string, parentPath = '') {
  const value = path.trim();
  const fullPath = value.startsWith('/')
    ? value
    : `${parentPath.replace(/\/$/, '')}/${value}`;
  const normalized = fullPath.replaceAll(/\/{2,}/g, '/').replace(/\/$/, '');
  return normalized || '/';
}

function relativeRoutePath(
  fullPath: string,
  parentPath: string,
  depth: number,
) {
  if (depth === 0) return fullPath;
  const prefix = `${parentPath.replace(/\/$/, '')}/`;
  return fullPath.startsWith(prefix)
    ? fullPath.slice(prefix.length)
    : fullPath.replace(/^\//, '');
}

function resolveComponent(
  isDirectory: boolean,
  fullPath: string,
  depth: number,
) {
  if (isDirectory) return depth === 0 ? 'BasicLayout' : undefined;
  if (!/^\/[a-z0-9][a-z0-9/_-]*$/i.test(fullPath) || fullPath.startsWith('/_core')) {
    return FALLBACK_COMPONENT;
  }
  return PAGE_COMPONENT_REGISTRY[fullPath] ?? `${fullPath}/index`;
}

function toRoute(
  menu: RbacMenu,
  parentPath = '',
  depth = 0,
): RouteRecordStringComponent {
  const fullPath = normalizeFullPath(menu.path, parentPath);
  const children = (menu.children || [])
    .filter((child) => child.menuType !== 2)
    .toSorted((left, right) => (left.sortNo || 0) - (right.sortNo || 0))
    .map((child) => toRoute(child, fullPath, depth + 1));
  const isDirectory = menu.menuType === 0 || children.length > 0;
  const component = resolveComponent(isDirectory, fullPath, depth);

  return {
    children: children.length > 0 ? children : undefined,
    ...(component ? { component } : {}),
    meta: {
      icon: menu.menuIconUrl?.trim() || undefined,
      order: menu.sortNo || 0,
      title: menu.menuName,
    },
    name: `RbacMenu${menu.id}`,
    path: relativeRoutePath(fullPath, parentPath, depth),
  } as RouteRecordStringComponent;
}

/** 获取由后端菜单树驱动、前端组件注册表约束的动态路由。 */
export async function getAllMenusApi() {
  const { menus } = await getCachedUserContext();
  return menus
    .filter((menu) => menu.menuType !== 2)
    .toSorted((left, right) => (left.sortNo || 0) - (right.sortNo || 0))
    .map((menu) => toRoute(menu));
}
