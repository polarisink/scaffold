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
  orgId: number;
  status: boolean;
  username: string;
}

export interface SysOrg extends AuditFields {
  children?: SysOrg[];
  orgCode: string;
  orgName: string;
  parentId: number;
  sort?: number;
}

export interface SysConfig extends AuditFields {
  configKey: string;
  configName: string;
  configValue: string;
  remark?: string;
  sysFlag: boolean;
}

export interface SysDictType extends AuditFields {
  dictName: string;
  dictType: string;
  remark?: string;
  status: boolean;
}

export type DictTagType = 'default' | 'error' | 'info' | 'success' | 'warning';

export interface SysDictData extends AuditFields {
  defaultFlag: boolean;
  dictLabel: string;
  dictSort: number;
  dictType: string;
  dictValue: string;
  remark?: string;
  status: boolean;
  tagType?: DictTagType;
}

export interface SysOperateLog extends AuditFields {
  action?: string;
  bizNo?: string;
  businessType?: string;
  costTime: number;
  errorMsg?: string;
  extra?: string;
  ip?: string;
  method?: string;
  operator?: string;
  param?: string;
  requestMethod?: string;
  result?: string;
  status: boolean;
  title?: string;
  url?: string;
}

export interface SysLoginLog extends AuditFields {
  action: 'LOGIN' | 'LOGOUT';
  ip?: string;
  message?: string;
  status: boolean;
  userAgent?: string;
  userId?: number;
  username?: string;
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
  orgId: number;
  password: string;
  roleIdList: number[];
  username: string;
}

export interface UserUpdateParams {
  id: number;
  orgId: number;
  roleIdList: number[];
  username: string;
}

export interface OrgParams {
  id?: number;
  orgCode: string;
  orgName: string;
  parentId: number;
  sort?: number;
}

export interface ConfigParams {
  configKey: string;
  configName: string;
  configValue: string;
  id?: number;
  remark?: string;
  sysFlag: boolean;
}

export interface DictTypeParams {
  dictName: string;
  dictType: string;
  id?: number;
  remark?: string;
  status: boolean;
}

export interface DictDataParams {
  defaultFlag: boolean;
  dictLabel: string;
  dictSort: number;
  dictType: string;
  dictValue: string;
  id?: number;
  remark?: string;
  status: boolean;
  tagType?: DictTagType;
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
  return requestClient.post<number>('/user', params);
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

export function getOrgTree() {
  return requestClient.get<SysOrg[]>('/org/tree');
}

export function createOrg(params: OrgParams) {
  return requestClient.post<number>('/org', params);
}

export function updateOrg(params: OrgParams & { id: number }) {
  return requestClient.put('/org', params);
}

export function deleteOrg(id: number) {
  return requestClient.delete(`/org/${id}`);
}

export function getConfigPage(params: {
  configKey?: string;
  configName?: string;
  pageNo: number;
  pageSize: number;
}) {
  return requestClient.post<PageResult<SysConfig>>('/config/page', params);
}

export function createConfig(params: ConfigParams) {
  return requestClient.post<number>('/config', params);
}

export function updateConfig(params: ConfigParams & { id: number }) {
  return requestClient.put('/config', params);
}

export function deleteConfig(id: number) {
  return requestClient.delete(`/config/${id}`);
}

export function getDictTypePage(params: {
  dictName?: string;
  dictType?: string;
  pageNo: number;
  pageSize: number;
  status?: boolean;
}) {
  return requestClient.post<PageResult<SysDictType>>('/dict/type/page', params);
}

export function getDictTypeOptions() {
  return requestClient.get<SysDictType[]>('/dict/type/options');
}

export function createDictType(params: DictTypeParams) {
  return requestClient.post<number>('/dict/type', params);
}

export function updateDictType(params: DictTypeParams & { id: number }) {
  return requestClient.put('/dict/type', params);
}

export function deleteDictType(id: number) {
  return requestClient.delete(`/dict/type/${id}`);
}

export function getDictDataPage(params: {
  dictLabel?: string;
  dictType?: string;
  pageNo: number;
  pageSize: number;
  status?: boolean;
}) {
  return requestClient.post<PageResult<SysDictData>>('/dict/data/page', params);
}

export function getDictDataByType(dictType: string) {
  return requestClient.get<SysDictData[]>(`/dict/data/type/${dictType}`);
}

export function createDictData(params: DictDataParams) {
  return requestClient.post<number>('/dict/data', params);
}

export function updateDictData(params: DictDataParams & { id: number }) {
  return requestClient.put('/dict/data', params);
}

export function deleteDictData(id: number) {
  return requestClient.delete(`/dict/data/${id}`);
}

export function getOperateLogPage(params: {
  operator?: string;
  pageNo: number;
  pageSize: number;
  status?: boolean;
  title?: string;
}) {
  return requestClient.post<PageResult<SysOperateLog>>(
    '/log/operation/page',
    params,
  );
}

export function deleteOperateLog(id: number) {
  return requestClient.delete(`/log/operation/${id}`);
}

export function cleanOperateLog() {
  return requestClient.delete('/log/operation/clean');
}

export function getLoginLogPage(params: {
  action?: string;
  ip?: string;
  pageNo: number;
  pageSize: number;
  status?: boolean;
  username?: string;
}) {
  return requestClient.post<PageResult<SysLoginLog>>('/log/login/page', params);
}

export function deleteLoginLog(id: number) {
  return requestClient.delete(`/log/login/${id}`);
}

export function cleanLoginLog() {
  return requestClient.delete('/log/login/clean');
}
