package com.scaffold.codegen.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GenTable {
    private Long id;
    private String tableName;
    private String tableComment;
    private String className;
    private String moduleName;
    private String businessName;
    private String packageName;
    private String author;
    private String databaseType;
    private String schemaName;
    private String frontendPath;
    private String backendPath;
    private String menuName;
    private String menuPath;
    private String permissionPrefix;
    private String defaultRoleCode;
    private List<GenColumn> columns = new ArrayList<>();
    private LocalDateTime gmtModified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public String getTableComment() { return tableComment; }
    public void setTableComment(String tableComment) { this.tableComment = tableComment; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getDatabaseType() { return databaseType; }
    public void setDatabaseType(String databaseType) { this.databaseType = databaseType; }
    public String getSchemaName() { return schemaName; }
    public void setSchemaName(String schemaName) { this.schemaName = schemaName; }
    public String getFrontendPath() { return frontendPath; }
    public void setFrontendPath(String frontendPath) { this.frontendPath = frontendPath; }
    public String getBackendPath() { return backendPath; }
    public void setBackendPath(String backendPath) { this.backendPath = backendPath; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getMenuPath() { return menuPath; }
    public void setMenuPath(String menuPath) { this.menuPath = menuPath; }
    public String getPermissionPrefix() { return permissionPrefix; }
    public void setPermissionPrefix(String permissionPrefix) { this.permissionPrefix = permissionPrefix; }
    public String getDefaultRoleCode() { return defaultRoleCode; }
    public void setDefaultRoleCode(String defaultRoleCode) { this.defaultRoleCode = defaultRoleCode; }
    public List<GenColumn> getColumns() { return columns; }
    public void setColumns(List<GenColumn> columns) { this.columns = columns; }
    public LocalDateTime getGmtModified() { return gmtModified; }
    public void setGmtModified(LocalDateTime gmtModified) { this.gmtModified = gmtModified; }
}
