import type { RouteRecordStringComponent } from '@vben/types';

import type { RbacMenu } from './user';

import { getCachedUserContext } from './user';

/**
 * 后端路由只能从这份注册表中选择组件，不能直接加载后端传入的文件路径。
 * 新增页面时，需要在这里显式注册对应的业务路由。
 */
const PAGE_COMPONENT_REGISTRY: Record<string, string> = {
  '/dashboard/analytics': '/dashboard/analytics/index',
  '/dashboard/workspace': '/dashboard/workspace/index',
  '/system/menu': '/system/menu/index',
  '/system/role': '/system/role/index',
  '/system/user': '/system/user/index',
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
  return PAGE_COMPONENT_REGISTRY[fullPath] ?? FALLBACK_COMPONENT;
}

function toRoute(
  menu: RbacMenu,
  parentPath = '',
  depth = 0,
): RouteRecordStringComponent {
  const fullPath = normalizeFullPath(menu.path, parentPath);
  const children = (menu.children || [])
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
    .toSorted((left, right) => (left.sortNo || 0) - (right.sortNo || 0))
    .map((menu) => toRoute(menu));
}
