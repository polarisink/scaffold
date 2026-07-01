import { requestClient } from '#/api/request';

export namespace AuthApi {
  /** 登录接口参数 */
  export interface LoginParams {
    password?: string;
    username?: string;
  }

  /** 登录接口返回值 */
}

/**
 * 登录
 */
export async function loginApi(data: AuthApi.LoginParams) {
  return requestClient.post<string>('/auth/login', data);
}

/**
 * 退出登录
 */
export async function logoutApi() {
  return requestClient.get('/auth/logout');
}

/**
 * 获取用户权限码
 */
export async function getAccessCodesApi() {
  const { getCachedUserContext } = await import('./user');
  const context = await getCachedUserContext();
  const codes: string[] = [];
  const visit = (menus: typeof context.menus) => {
    menus.forEach((menu) => {
      if (menu.menuUrl?.trim()) codes.push(menu.menuUrl.trim());
      if (menu.children?.length) visit(menu.children);
    });
  };
  visit(context.menus);
  return [...new Set(codes)];
}
