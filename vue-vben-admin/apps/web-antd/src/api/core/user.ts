import type { UserInfo } from '@vben/types';

import { requestClient } from '#/api/request';

export interface UpdatePasswordParams {
  newPasswd: string;
  oldPasswd: string;
}

/**
 * 获取用户信息
 */
export async function getUserInfoApi() {
  return requestClient.get<UserInfo>('/user/info');
}

/** 修改当前登录用户的密码 */
export async function updatePasswordApi(data: UpdatePasswordParams) {
  return requestClient.post('/user/passwd/update', data);
}
