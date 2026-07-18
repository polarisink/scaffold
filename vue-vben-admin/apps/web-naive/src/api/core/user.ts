import type { UserInfo } from '@vben/types';

import { requestClient } from '#/api/request';

export interface RbacMenu {
  children?: RbacMenu[];
  id: number;
  menuIconUrl?: string;
  menuName: string;
  menuType: number;
  menuUrl?: string;
  parentId: number;
  path: string;
  sortNo?: number;
}

export interface RbacUser {
  gmtCreated?: string;
  gmtModified?: string;
  id: number;
  orgId: number;
  status: boolean;
  username: string;
}

export interface RbacOrg {
  id: number;
  orgCode: string;
  orgName: string;
  parentId: number;
  sort?: number;
}

export interface RbacRole {
  description?: string;
  id: number;
  roleCode: string;
  roleName: string;
}

export interface RbacUserContext {
  menus: RbacMenu[];
  org?: RbacOrg;
  roles: RbacRole[];
  user: RbacUser;
}

let contextPromise: null | Promise<RbacUserContext> = null;

export function getCachedUserContext() {
  contextPromise ??= requestClient.get<RbacUserContext>('/user');
  return contextPromise;
}

export function clearCachedUserContext() {
  contextPromise = null;
}

/**
 * 获取用户信息
 */
export async function getUserInfoApi() {
  const { org, roles, user } = await getCachedUserContext();
  return {
    avatar: '',
    desc: `组织：${org?.orgName || '未分配'}`,
    homePath: '/dashboard/analytics',
    realName: user.username,
    roles: roles.map((role) => role.roleCode),
    token: '',
    userId: String(user.id),
    username: user.username,
  } satisfies UserInfo;
}
