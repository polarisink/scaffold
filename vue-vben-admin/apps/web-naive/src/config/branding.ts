import { reactive } from 'vue';

import { useAppConfig } from '@vben/hooks';
import { preferences, updatePreferences } from '@vben/preferences';

export interface BrandingConfig {
  appName: string;
  loginDescription: string;
  loginImageUrl: string;
  loginTitle: string;
  logoUrl: string;
}

export const branding = reactive<BrandingConfig>({
  appName: import.meta.env.VITE_APP_TITLE,
  loginDescription: '',
  loginImageUrl: '',
  loginTitle: '',
  logoUrl: '/scaffold-logo.webp',
});

function nonBlank(value: unknown): string | undefined {
  if (typeof value !== 'string') return undefined;
  const normalized = value.trim();
  return normalized || undefined;
}

export async function loadBranding(): Promise<void> {
  const { apiURL } = useAppConfig(import.meta.env, import.meta.env.PROD);
  const endpoint = `${apiURL.replace(/\/$/, '')}/config/branding`;

  try {
    const response = await fetch(endpoint, {
      signal: AbortSignal.timeout(3000),
    });
    if (!response.ok) return;

    const body = (await response.json()) as Partial<BrandingConfig> & {
      code?: number;
      data?: Partial<BrandingConfig>;
    };
    const remote = body.code === 0 ? body.data : body;
    if (!remote) return;

    branding.appName = nonBlank(remote.appName) ?? branding.appName;
    branding.logoUrl = nonBlank(remote.logoUrl) ?? branding.logoUrl;
    branding.loginImageUrl =
      nonBlank(remote.loginImageUrl) ?? branding.loginImageUrl;
    branding.loginTitle = nonBlank(remote.loginTitle) ?? branding.loginTitle;
    branding.loginDescription =
      nonBlank(remote.loginDescription) ?? branding.loginDescription;

    updatePreferences({
      app: { name: branding.appName },
      logo: {
        source: branding.logoUrl || preferences.logo.source,
        sourceDark: branding.logoUrl || preferences.logo.sourceDark,
      },
    });
  } catch {
    // 后端未启动、超时或响应格式异常时保留前端内置品牌信息。
  }
}
