import { requestClient } from '#/api/request';

import type { RbacUserContext } from './core/user';

export interface PageResult<T> {
  current: number;
  pages: number;
  records: T[];
  size: number;
  total: number;
}

export interface AuditFields {
  createdBy?: number;
  gmtCreated?: string;
  gmtModified?: string;
  id: number;
  modifiedBy?: number;
}

export interface SysUser extends AuditFields {
  orgId: string;
  status: boolean;
  username: string;
}

export interface SysRole extends AuditFields {
  description?: string;
  roleCode: string;
  roleName: string;
}

export interface SysMenu extends AuditFields {
  children?: SysMenu[];
  menuIconUrl?: string;
  menuName: string;
  menuType: number;
  menuUrl?: string;
  parentId: number;
  path: string;
  sortNo?: number;
}

export interface UserCreateParams {
  orgId: string;
  password: string;
  positionId?: string;
  roleIdList: number[];
  username: string;
}

export interface UserUpdateParams {
  id: number;
  orgId: string;
  roleIdList: number[];
  username: string;
}

export interface RoleParams {
  description?: string;
  id?: number;
  menuIdList: number[];
  roleCode: string;
  roleName: string;
}

export interface MenuParams {
  id?: number;
  menuIconUrl?: string;
  menuName: string;
  menuType: number;
  menuUrl?: string;
  parentId: number | string;
  path: string;
  sortNo?: number;
}

export function getUserPage(params: {
  pageNo: number;
  pageSize: number;
  username?: string;
}) {
  return requestClient.post<PageResult<SysUser>>('/user/page', params);
}

export function createUser(params: UserCreateParams) {
  return requestClient.post<string>('/user', params);
}

export function getUserDetail(userId: number) {
  return requestClient.get<RbacUserContext>('/user', { params: { userId } });
}

export function updateUser(params: UserUpdateParams) {
  return requestClient.put('/user', params);
}

export function deleteUser(id: number) {
  return requestClient.delete(`/user/${id}`);
}

export function toggleUserStatus(id: number) {
  return requestClient.post(`/user/${id}`);
}

export function resetUserPassword(id: number) {
  return requestClient.post(`/user/passwd/reset/${id}`);
}

export function getRolePage(params: {
  pageNo: number;
  pageSize: number;
  roleCode?: string;
  roleName?: string;
}) {
  return requestClient.post<PageResult<SysRole>>('/role/page', params);
}

export function getRoleDetail(id: number) {
  return requestClient.get<{ list: number[]; tree: SysMenu[] }>(`/role/${id}`);
}

export function createRole(params: RoleParams) {
  return requestClient.post('/role', params);
}

export function updateRole(params: RoleParams & { id: number }) {
  return requestClient.put('/role', params);
}

export function deleteRole(id: number) {
  return requestClient.delete(`/role/${id}`);
}

export function getMenuTree() {
  return requestClient.get<SysMenu[]>('/menu/tree');
}

export function createMenu(params: MenuParams) {
  return requestClient.post<string>('/menu', params);
}

export function updateMenu(params: MenuParams & { id: number }) {
  return requestClient.put('/menu', params);
}

export function deleteMenu(id: number) {
  return requestClient.delete(`/menu/${id}`);
}
