import { requestClient } from '#/api/request';
import type { ${className}, ${className}Save } from './${businessName}.model';

const baseUrl = '${apiPrefix}';

export const list${className} = () => requestClient.get<${className}[]>(baseUrl);
export const get${className} = (id: number) => requestClient.get<${className}>(`${r"${baseUrl}"}/${r"${id}"}`);
export const create${className} = (data: ${className}Save) => requestClient.post(baseUrl, data);
export const update${className} = (id: number, data: ${className}Save) => requestClient.put(`${r"${baseUrl}"}/${r"${id}"}`, data);
export const delete${className} = (id: number) => requestClient.delete(`${r"${baseUrl}"}/${r"${id}"}`);
