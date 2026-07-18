import { requestClient } from '#/api/request';

export interface GenColumn {
  id?: number;
  columnName: string;
  propertyName: string;
  jdbcType: string;
  columnType: string;
  javaType: string;
  tsType: string;
  columnComment?: string;
  columnLength?: number;
  numericPrecision?: number;
  numericScale?: number;
  nullable: boolean;
  primaryKey: boolean;
  autoIncrement: boolean;
  uniqueKey: boolean;
  queryable: boolean;
  queryType: 'EQ' | 'LIKE';
  listVisible: boolean;
  formVisible: boolean;
  formWidget: string;
  dictType?: string;
  sortNo: number;
}

export interface CodegenConfig {
  id?: number;
  tableName: string;
  tableComment?: string;
  className: string;
  moduleName: string;
  businessName: string;
  packageName: string;
  author: string;
  databaseType: string;
  schemaName?: string;
  frontendPath: string;
  backendPath: string;
  menuName: string;
  columns: GenColumn[];
  gmtModified?: string;
}

export interface DatabaseTable {
  catalog?: string;
  schema?: string;
  name: string;
  comment?: string;
  type: string;
}

export const getCodegenConfigs = () => requestClient.get<CodegenConfig[]>('/codegen/configs');
export const getCodegenConfig = (id: number) => requestClient.get<CodegenConfig>(`/codegen/configs/${id}`);
export const createCodegenConfig = (data: CodegenConfig) => requestClient.post<number>('/codegen/configs', data);
export const updateCodegenConfig = (id: number, data: CodegenConfig) => requestClient.put(`/codegen/configs/${id}`, data);
export const deleteCodegenConfig = (id: number) => requestClient.delete(`/codegen/configs/${id}`);
export const getDatabaseTables = (name = '') => requestClient.get<DatabaseTable[]>('/codegen/database/tables', { params: { name } });
export const importDatabaseTable = (tableName: string, schema?: string) => requestClient.post<number>('/codegen/database/import', undefined, { params: { schema, tableName } });
export const downloadCodegen = (id: number) => requestClient.get<Blob>(`/codegen/configs/${id}/download`, { responseReturn: 'body', responseType: 'blob' });

export type GenTable = CodegenConfig;
export const getCodegenTables = getCodegenConfigs;
export const getCodegenTable = getCodegenConfig;
export const createCodegenTable = createCodegenConfig;
export const updateCodegenTable = updateCodegenConfig;
export const deleteCodegenTable = deleteCodegenConfig;
