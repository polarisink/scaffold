import { requestClient } from '#/api/request';
import type { ${className}, ${className}Save } from './${businessName}.model';

const baseUrl = '${apiPrefix}';

export interface PageResponse<T> {
    current: number;
    pages: number;
    records: T[];
    size: number;
    total: number;
}

export interface ${className}PageQuery {
    pageNo: number;
    pageSize: number;
}

export const page${className} = (data: ${className}PageQuery) =>
    requestClient.post<PageResponse<${className}>>(`${r"${baseUrl}"}/page`, data);
export const get${className} = (id: number) => requestClient.get<${className}>(`${r"${baseUrl}"}/${r"${id}"}`);
export const create${className} = (data: ${className}Save) => requestClient.post(baseUrl, data);
export const update${className} = (id: number, data: ${className}Save) => requestClient.put(`${r"${baseUrl}"}/${r"${id}"}`, data);
export const delete${className} = (id: number) => requestClient.delete(`${r"${baseUrl}"}/${r"${id}"}`);
