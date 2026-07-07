package com.scaffold.codegen.service;

import com.scaffold.codegen.entity.GenColumnEntity;
import com.scaffold.codegen.entity.GenTableEntity;
import com.scaffold.codegen.model.DatabaseTable;
import com.scaffold.codegen.model.GenColumn;
import com.scaffold.codegen.model.GenTable;
import com.scaffold.codegen.repository.GenColumnRepository;
import com.scaffold.codegen.repository.GenTableRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class LightCodegenService {

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;

    private final GenTableRepository tableRepository;
    private final GenColumnRepository columnRepository;
    private final DataSource dataSource;
    private final Configuration freemarker;

    public LightCodegenService(GenTableRepository tableRepository,
                               GenColumnRepository columnRepository,
                               DataSource dataSource) {
        this.tableRepository = tableRepository;
        this.columnRepository = columnRepository;
        this.dataSource = dataSource;
        this.freemarker = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarker.setDefaultEncoding(StandardCharsets.UTF_8.name());
        this.freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.freemarker.setLogTemplateExceptions(false);
        this.freemarker.setWrapUncheckedExceptions(true);
    }

    public List<GenTable> listTables() {
        return tableRepository.findByDeletedOrderByGmtModifiedDescIdDesc(NOT_DELETED)
                .stream()
                .map(entity -> toModel(entity, List.of()))
                .toList();
    }

    public GenTable getTable(Long id) {
        GenTableEntity table = tableRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new IllegalArgumentException("生成配置不存在"));
        List<GenColumnEntity> columns = columnRepository.findByTableIdAndDeletedOrderBySortNoAscIdAsc(id, NOT_DELETED);
        return toModel(table, columns);
    }

    @Transactional
    public Long create(GenTable table) {
        fillDefaults(table);
        GenTableEntity saved = tableRepository.save(toEntity(table, new GenTableEntity()));
        saveColumns(saved.getId(), table.getColumns());
        return saved.getId();
    }

    @Transactional
    public void update(Long id, GenTable table) {
        fillDefaults(table);
        table.setId(id);
        GenTableEntity entity = tableRepository.findById(id).orElseGet(GenTableEntity::new);
        entity.setId(id);
        tableRepository.save(toEntity(table, entity));
        List<GenColumnEntity> oldColumns = columnRepository.findByTableIdIn(List.of(id));
        oldColumns.forEach(column -> {
            column.setDeleted(DELETED);
            column.setGmtModified(LocalDateTime.now());
        });
        columnRepository.saveAll(oldColumns);
        saveColumns(id, table.getColumns());
    }

    @Transactional
    public void delete(Long id) {
        tableRepository.findById(id).ifPresent(table -> {
            table.setDeleted(DELETED);
            table.setGmtModified(LocalDateTime.now());
            tableRepository.save(table);
        });
        List<GenColumnEntity> columns = columnRepository.findByTableIdIn(List.of(id));
        columns.forEach(column -> {
            column.setDeleted(DELETED);
            column.setGmtModified(LocalDateTime.now());
        });
        columnRepository.saveAll(columns);
    }

    public List<DatabaseTable> listDatabaseTables(String name) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();
            String pattern = StringUtils.hasText(name) ? "%" + name + "%" : "%";
            List<DatabaseTable> rows = new ArrayList<>();
            try (ResultSet rs = metaData.getTables(catalog, schema, pattern, new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if (isIgnoredTable(tableName)) {
                        continue;
                    }
                    rows.add(new DatabaseTable(
                            rs.getString("TABLE_CAT"),
                            rs.getString("TABLE_SCHEM"),
                            tableName,
                            tableComment(rs.getString("REMARKS")),
                            rs.getString("TABLE_TYPE")
                    ));
                }
            }
            rows.sort(Comparator.comparing(DatabaseTable::name));
            return rows;
        } catch (SQLException e) {
            throw new IllegalStateException("读取数据库表失败", e);
        }
    }

    @Transactional
    public Long importTable(String tableName, String schema) {
        try (Connection connection = dataSource.getConnection()) {
            GenTable table = buildTableFromMetadata(connection, tableName, schema);
            Optional<GenTableEntity> existed = tableRepository.findFirstByTableNameOrderByDeletedAscIdDesc(tableName);
            if (existed.isPresent()) {
                update(existed.get().getId(), table);
                return existed.get().getId();
            }
            return create(table);
        } catch (SQLException e) {
            throw new IllegalStateException("导入数据库表失败：" + tableName, e);
        }
    }

    public byte[] download(Long id) {
        GenTable table = getTable(id);
        Map<String, Object> model = buildModel(table);
        List<CodegenTemplate> templates = builtinTemplates(table);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(bos, StandardCharsets.UTF_8)) {
            for (CodegenTemplate item : templates) {
                String path = renderString("path-" + item.name(), item.path(), model);
                String content = renderTemplateFile(item.templatePath(), model);
                zip.putNextEntry(new ZipEntry(path));
                zip.write(content.getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
            zip.finish();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("生成 ZIP 失败", e);
        }
    }

    private GenTable buildTableFromMetadata(Connection connection, String tableName, String schema) throws SQLException {
        String catalog = connection.getCatalog();
        DatabaseMetaData metaData = connection.getMetaData();
        String actualSchema = StringUtils.hasText(schema) ? schema : connection.getSchema();
        String comment = "";
        GenTable table = new GenTable();
        table.setTableName(tableName);
        table.setTableComment(comment);
        table.setClassName(toPascal(stripTablePrefix(tableName)));
        table.setModuleName("system");
        table.setBusinessName(toKebab(stripTablePrefix(tableName)));
        table.setPackageName("com.scaffold.system");
        table.setAuthor("scaffold");
        table.setDatabaseType(databaseType(metaData.getDatabaseProductName()));
        table.setSchemaName(actualSchema);
        table.setFrontendPath("vue-vben-admin/apps/web-naive/src");
        table.setBackendPath("scaffold-biz/src/main/java");
        table.setMenuName(StringUtils.hasText(comment) ? comment : table.getClassName());
        table.setMenuPath("/" + table.getModuleName() + "/" + table.getBusinessName());
        table.setPermissionPrefix(table.getModuleName() + ":" + table.getBusinessName());
        table.setDefaultRoleCode("admin");

        Set<String> primaryKeys = new HashSet<>();
        try (ResultSet pk = metaData.getPrimaryKeys(catalog, actualSchema, tableName)) {
            while (pk.next()) {
                primaryKeys.add(pk.getString("COLUMN_NAME"));
            }
        }
        Set<String> uniqueKeys = uniqueColumns(metaData, catalog, actualSchema, tableName, primaryKeys);

        List<GenColumn> columns = new ArrayList<>();
        try (ResultSet rs = metaData.getColumns(catalog, actualSchema, tableName, "%")) {
            while (rs.next()) {
                GenColumn column = new GenColumn();
                column.setColumnName(rs.getString("COLUMN_NAME"));
                column.setPropertyName(toCamel(column.getColumnName()));
                column.setJdbcType(JDBCType.valueOf(rs.getInt("DATA_TYPE")).getName());
                column.setColumnType(rs.getString("TYPE_NAME").toLowerCase(Locale.ROOT)
                        + typeSuffix(rs.getString("TYPE_NAME"), rs.getInt("COLUMN_SIZE"), rs.getInt("DECIMAL_DIGITS")));
                column.setColumnComment(rs.getString("REMARKS"));
                column.setColumnLength(rs.getInt("COLUMN_SIZE"));
                column.setNumericPrecision(rs.getInt("COLUMN_SIZE"));
                column.setNumericScale(rs.getInt("DECIMAL_DIGITS"));
                column.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                column.setPrimaryKey(primaryKeys.contains(column.getColumnName()));
                column.setAutoIncrement("YES".equalsIgnoreCase(rs.getString("IS_AUTOINCREMENT")));
                column.setUniqueKey(uniqueKeys.contains(column.getColumnName()));
                column.setJavaType(javaType(rs.getInt("DATA_TYPE")));
                column.setTsType(tsType(column.getJavaType()));
                column.setQueryable(!column.getPrimaryKey() && isTextType(column.getJavaType()));
                column.setQueryType(isTextType(column.getJavaType()) ? "LIKE" : "EQ");
                column.setListVisible(!List.of("deleted").contains(column.getColumnName()));
                column.setFormVisible(!column.getPrimaryKey() && !List.of("gmt_created", "gmt_modified", "created_by", "modified_by", "deleted").contains(column.getColumnName()));
                column.setFormWidget(formWidget(column.getJavaType(), column.getColumnName()));
                column.setSortNo(columns.size());
                columns.add(column);
            }
        }
        table.setColumns(columns);
        return table;
    }

    private GenTable toModel(GenTableEntity entity, List<GenColumnEntity> columnEntities) {
        GenTable table = new GenTable();
        table.setId(entity.getId());
        table.setTableName(entity.getTableName());
        table.setTableComment(entity.getTableComment());
        table.setClassName(entity.getClassName());
        table.setModuleName(entity.getModuleName());
        table.setBusinessName(entity.getBusinessName());
        table.setPackageName(entity.getPackageName());
        table.setAuthor(entity.getAuthor());
        table.setDatabaseType(entity.getDatabaseType());
        table.setSchemaName(entity.getSchemaName());
        table.setFrontendPath(entity.getFrontendPath());
        table.setBackendPath(entity.getBackendPath());
        table.setMenuName(entity.getMenuName());
        table.setMenuPath(entity.getMenuPath());
        table.setPermissionPrefix(entity.getPermissionPrefix());
        table.setDefaultRoleCode(entity.getDefaultRoleCode());
        table.setGmtModified(entity.getGmtModified());
        table.setColumns(columnEntities.stream().map(this::toModel).toList());
        return table;
    }

    private GenColumn toModel(GenColumnEntity entity) {
        GenColumn column = new GenColumn();
        column.setId(entity.getId());
        column.setColumnName(entity.getColumnName());
        column.setPropertyName(entity.getPropertyName());
        column.setJdbcType(entity.getJdbcType());
        column.setColumnType(entity.getColumnType());
        column.setJavaType(entity.getJavaType());
        column.setTsType(entity.getTsType());
        column.setColumnComment(entity.getColumnComment());
        column.setColumnLength(entity.getColumnLength());
        column.setNumericPrecision(entity.getNumericPrecision());
        column.setNumericScale(entity.getNumericScale());
        column.setNullable(entity.getNullable());
        column.setPrimaryKey(entity.getPrimaryKey());
        column.setAutoIncrement(entity.getAutoIncrement());
        column.setUniqueKey(entity.getUniqueKey());
        column.setQueryable(entity.getQueryable());
        column.setQueryType(entity.getQueryType());
        column.setListVisible(entity.getListVisible());
        column.setFormVisible(entity.getFormVisible());
        column.setFormWidget(entity.getFormWidget());
        column.setDictType(entity.getDictType());
        column.setSortNo(entity.getSortNo());
        return column;
    }

    private GenTableEntity toEntity(GenTable table, GenTableEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        if (entity.getId() == null) {
            entity.setGmtCreated(now);
        }
        entity.setGmtModified(now);
        entity.setTableName(table.getTableName());
        entity.setTableComment(table.getTableComment());
        entity.setClassName(table.getClassName());
        entity.setModuleName(table.getModuleName());
        entity.setBusinessName(table.getBusinessName());
        entity.setPackageName(table.getPackageName());
        entity.setAuthor(table.getAuthor());
        entity.setDatabaseType(table.getDatabaseType());
        entity.setSchemaName(table.getSchemaName());
        entity.setFrontendPath(table.getFrontendPath());
        entity.setBackendPath(table.getBackendPath());
        entity.setMenuName(table.getMenuName());
        entity.setMenuPath(table.getMenuPath());
        entity.setPermissionPrefix(table.getPermissionPrefix());
        entity.setDefaultRoleCode(table.getDefaultRoleCode());
        entity.setGenerateTable(false);
        entity.setGenerateMenu(false);
        entity.setDeleted(NOT_DELETED);
        return entity;
    }

    private void saveColumns(Long tableId, List<GenColumn> columns) {
        if (columns == null) {
            return;
        }
        List<GenColumnEntity> entities = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            GenColumn column = columns.get(i);
            fillColumnDefaults(column, i);
            GenColumnEntity entity = new GenColumnEntity();
            entity.setTableId(tableId);
            entity.setColumnName(column.getColumnName());
            entity.setPropertyName(column.getPropertyName());
            entity.setJdbcType(column.getJdbcType());
            entity.setColumnType(column.getColumnType());
            entity.setJavaType(column.getJavaType());
            entity.setTsType(column.getTsType());
            entity.setColumnComment(column.getColumnComment());
            entity.setColumnLength(column.getColumnLength());
            entity.setNumericPrecision(column.getNumericPrecision());
            entity.setNumericScale(column.getNumericScale());
            entity.setNullable(column.getNullable());
            entity.setPrimaryKey(column.getPrimaryKey());
            entity.setAutoIncrement(column.getAutoIncrement());
            entity.setUniqueKey(column.getUniqueKey());
            entity.setQueryable(column.getQueryable());
            entity.setQueryType(column.getQueryType());
            entity.setListVisible(column.getListVisible());
            entity.setFormVisible(column.getFormVisible());
            entity.setFormWidget(column.getFormWidget());
            entity.setDictType(column.getDictType());
            entity.setSortNo(column.getSortNo());
            entity.setDeleted(NOT_DELETED);
            entity.setGmtCreated(LocalDateTime.now());
            entity.setGmtModified(LocalDateTime.now());
            entities.add(entity);
        }
        columnRepository.saveAll(entities);
    }

    private void fillDefaults(GenTable table) {
        table.setTableName(required(table.getTableName(), "表名不能为空"));
        String businessName = StringUtils.hasText(table.getBusinessName()) ? table.getBusinessName() : toKebab(stripTablePrefix(table.getTableName()));
        String className = StringUtils.hasText(table.getClassName()) ? table.getClassName() : toPascal(stripTablePrefix(table.getTableName()));
        String moduleName = StringUtils.hasText(table.getModuleName()) ? table.getModuleName() : "system";
        table.setBusinessName(businessName);
        table.setClassName(className);
        table.setModuleName(moduleName);
        table.setPackageName(defaultIfBlank(table.getPackageName(), "com.scaffold." + moduleName));
        table.setAuthor(defaultIfBlank(table.getAuthor(), "scaffold"));
        table.setDatabaseType(defaultIfBlank(table.getDatabaseType(), "mysql"));
        table.setFrontendPath(defaultIfBlank(table.getFrontendPath(), "vue-vben-admin/apps/web-naive/src"));
        table.setBackendPath(defaultIfBlank(table.getBackendPath(), "scaffold-biz/src/main/java"));
        table.setMenuName(defaultIfBlank(table.getMenuName(), StringUtils.hasText(table.getTableComment()) ? table.getTableComment() : className));
        table.setMenuPath(defaultIfBlank(table.getMenuPath(), "/" + moduleName + "/" + businessName));
        table.setPermissionPrefix(defaultIfBlank(table.getPermissionPrefix(), moduleName + ":" + businessName));
        table.setDefaultRoleCode(defaultIfBlank(table.getDefaultRoleCode(), "admin"));
    }

    private void fillColumnDefaults(GenColumn column, int index) {
        column.setColumnName(required(column.getColumnName(), "字段名不能为空"));
        column.setPropertyName(defaultIfBlank(column.getPropertyName(), toCamel(column.getColumnName())));
        column.setColumnType(defaultIfBlank(column.getColumnType(), "varchar(255)"));
        column.setJdbcType(defaultIfBlank(column.getJdbcType(), column.getColumnType().replaceAll("\\(.*", "").toUpperCase(Locale.ROOT)));
        column.setJavaType(defaultIfBlank(column.getJavaType(), "String"));
        column.setTsType(defaultIfBlank(column.getTsType(), tsType(column.getJavaType())));
        column.setNullable(column.getNullable() == null || column.getNullable());
        column.setPrimaryKey(column.getPrimaryKey() != null && column.getPrimaryKey());
        column.setAutoIncrement(column.getAutoIncrement() != null && column.getAutoIncrement());
        column.setUniqueKey(column.getUniqueKey() != null && column.getUniqueKey() && !column.getPrimaryKey());
        column.setQueryable(column.getQueryable() != null && column.getQueryable());
        column.setQueryType(defaultIfBlank(column.getQueryType(), "EQ"));
        column.setListVisible(column.getListVisible() == null || column.getListVisible());
        column.setFormVisible(column.getFormVisible() == null || column.getFormVisible());
        column.setFormWidget(defaultIfBlank(column.getFormWidget(), "Input"));
        column.setSortNo(column.getSortNo() == null ? index : column.getSortNo());
    }

    private Map<String, Object> buildModel(GenTable table) {
        GenColumn pk = table.getColumns().stream().filter(c -> Boolean.TRUE.equals(c.getPrimaryKey())).findFirst()
                .orElseGet(() -> table.getColumns().isEmpty() ? null : table.getColumns().get(0));
        List<GenColumn> businessColumns = table.getColumns().stream()
                .filter(column -> !isBaseAuditableColumn(column))
                .toList();
        List<GenColumn> uniqueColumns = businessColumns.stream()
                .filter(column -> Boolean.TRUE.equals(column.getUniqueKey()))
                .toList();
        Set<String> imports = new TreeSet<>();
        for (GenColumn column : table.getColumns()) {
            if (column.getJavaType() != null && column.getJavaType().contains(".")) {
                imports.add(column.getJavaType());
            }
        }
        Set<String> entityImports = new TreeSet<>();
        for (GenColumn column : businessColumns) {
            if (column.getJavaType() != null && column.getJavaType().contains(".")) {
                entityImports.add(column.getJavaType());
            }
        }
        Set<String> queryImports = new TreeSet<>();
        Set<String> createImports = new TreeSet<>();
        for (GenColumn column : table.getColumns()) {
            if (Boolean.TRUE.equals(column.getQueryable())
                    && column.getJavaType() != null
                    && column.getJavaType().contains(".")) {
                queryImports.add(column.getJavaType());
            }
            if (Boolean.TRUE.equals(column.getFormVisible())
                    && column.getJavaType() != null
                    && column.getJavaType().contains(".")) {
                createImports.add(column.getJavaType());
            }
        }
        Map<String, Object> model = new HashMap<>();
        model.put("table", table);
        model.put("columns", table.getColumns());
        model.put("businessColumns", businessColumns);
        model.put("uniqueColumns", uniqueColumns);
        model.put("hasUniqueColumns", !uniqueColumns.isEmpty());
        model.put("pk", pk);
        model.put("imports", imports);
        model.put("entityImports", entityImports);
        model.put("queryImports", queryImports);
        model.put("createImports", createImports);
        model.put("packagePath", table.getPackageName().replace('.', '/'));
        model.put("className", table.getClassName());
        model.put("lowerClassName", lowerFirst(table.getClassName()));
        model.put("moduleName", table.getModuleName());
        model.put("businessName", table.getBusinessName());
        model.put("javaBusinessName", toJavaPackageSegment(table.getBusinessName()));
        model.put("apiPrefix", "/" + table.getModuleName() + "/" + table.getBusinessName());
        model.put("author", table.getAuthor());
        model.put("now", LocalDateTime.now().toString());
        return model;
    }

    private boolean isBaseAuditableColumn(GenColumn column) {
        String columnName = column.getColumnName();
        String propertyName = column.getPropertyName();
        return Set.of("id", "gmt_modified", "gmt_created", "created_by", "modified_by", "deleted").contains(columnName)
                || Set.of("id", "gmtModified", "gmtCreated", "createdBy", "modifiedBy", "deleted").contains(propertyName);
    }

    private List<CodegenTemplate> builtinTemplates(GenTable table) {
        List<CodegenTemplate> list = new ArrayList<>();
        String javaBase = "${table.backendPath}/${packagePath}/${javaBusinessName}";
        list.add(new CodegenTemplate("entity", javaBase + "/${className}.java", "codegen/templates/entity.java.ftl"));
        list.add(new CodegenTemplate("query", javaBase + "/${className}QueryDTO.java", "codegen/templates/query-dto.java.ftl"));
        list.add(new CodegenTemplate("create", javaBase + "/${className}CreateDTO.java", "codegen/templates/create-dto.java.ftl"));
        list.add(new CodegenTemplate("mapper", javaBase + "/${className}Mapper.java", "codegen/templates/mapper.java.ftl"));
        list.add(new CodegenTemplate("service", javaBase + "/${className}Service.java", "codegen/templates/service.java.ftl"));
        list.add(new CodegenTemplate("controller", javaBase + "/${className}Controller.java", "codegen/templates/controller.java.ftl"));
        String frontBase = "${table.frontendPath}/views/${moduleName}/${businessName}";
        list.add(new CodegenTemplate("frontend-api", "${table.frontendPath}/api/${moduleName}/${businessName}.ts", "codegen/templates/frontend-api.ts.ftl"));
        list.add(new CodegenTemplate("frontend-type", "${table.frontendPath}/api/${moduleName}/${businessName}.model.ts", "codegen/templates/frontend-type.ts.ftl"));
        list.add(new CodegenTemplate("frontend-page", frontBase + "/index.vue", "codegen/templates/frontend-page-naive.vue.ftl"));
        list.add(new CodegenTemplate("frontend-page-ele", frontBase + "/index.ele.vue", "codegen/templates/frontend-page-ele.vue.ftl"));
        list.add(new CodegenTemplate("frontend-page-antd", frontBase + "/index.antd.vue", "codegen/templates/frontend-page-antd.vue.ftl"));
        return list;
    }

    private String renderString(String name, String source, Map<String, Object> model) throws Exception {
        Template template = new Template(name, new StringReader(source), freemarker);
        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

    private String renderTemplateFile(String templatePath, Map<String, Object> model) throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (input == null) {
                throw new IllegalStateException("模板文件不存在：" + templatePath);
            }
            try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                Template template = new Template(templatePath, reader, freemarker);
                StringWriter writer = new StringWriter();
                template.process(model, writer);
                return writer.toString();
            }
        }
    }

    private String tableComment(String fallback) {
        return StringUtils.hasText(fallback) ? fallback : "";
    }

    private Set<String> uniqueColumns(DatabaseMetaData metaData,
                                      String catalog,
                                      String schema,
                                      String tableName,
                                      Set<String> primaryKeys) throws SQLException {
        Map<String, List<String>> indexColumns = new HashMap<>();
        try (ResultSet rs = metaData.getIndexInfo(catalog, schema, tableName, true, false)) {
            while (rs.next()) {
                short type = rs.getShort("TYPE");
                String columnName = rs.getString("COLUMN_NAME");
                String indexName = rs.getString("INDEX_NAME");
                if (type == DatabaseMetaData.tableIndexStatistic
                        || !StringUtils.hasText(indexName)
                        || !StringUtils.hasText(columnName)
                        || primaryKeys.contains(columnName)) {
                    continue;
                }
                indexColumns.computeIfAbsent(indexName, key -> new ArrayList<>()).add(columnName);
            }
        }
        Set<String> columns = new HashSet<>();
        indexColumns.values().stream()
                .filter(items -> items.size() == 1)
                .map(List::getFirst)
                .forEach(columns::add);
        return columns;
    }

    private boolean isIgnoredTable(String tableName) {
        return tableName == null
                || tableName.startsWith("gen_")
                || tableName.equalsIgnoreCase("flyway_schema_history");
    }

    private String databaseType(String productName) {
        return productName == null ? "mysql" : productName.toLowerCase(Locale.ROOT).replace(" ", "-");
    }

    private String javaType(int jdbcType) {
        return switch (jdbcType) {
            case Types.BIGINT -> "Long";
            case Types.INTEGER, Types.SMALLINT, Types.TINYINT -> "Integer";
            case Types.DECIMAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.REAL -> "java.math.BigDecimal";
            case Types.BOOLEAN, Types.BIT -> "Boolean";
            case Types.DATE -> "java.time.LocalDate";
            case Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE, Types.TIME, Types.TIME_WITH_TIMEZONE -> "java.time.LocalDateTime";
            default -> "String";
        };
    }

    private String tsType(String javaType) {
        if ("Boolean".equals(javaType)) {
            return "boolean";
        }
        if (List.of("Integer", "Long", "java.math.BigDecimal").contains(javaType)) {
            return "number";
        }
        return "string";
    }

    private String formWidget(String javaType, String columnName) {
        if ("Boolean".equals(javaType)) {
            return "Switch";
        }
        if (javaType != null && javaType.startsWith("java.time")) {
            return "DatePicker";
        }
        if (columnName != null && (columnName.endsWith("_desc") || columnName.endsWith("_remark"))) {
            return "Textarea";
        }
        return "Input";
    }

    private boolean isTextType(String javaType) {
        return "String".equals(javaType);
    }

    private String typeSuffix(String typeName, int size, int scale) {
        String type = typeName == null ? "" : typeName.toLowerCase(Locale.ROOT);
        if (List.of("varchar", "char", "decimal", "numeric").contains(type) && size > 0) {
            return scale > 0 ? "(" + size + "," + scale + ")" : "(" + size + ")";
        }
        return "";
    }

    private String stripTablePrefix(String tableName) {
        return tableName.replaceFirst("^(sys|biz|t)_", "");
    }

    private String toCamel(String value) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;
        for (char c : value.toCharArray()) {
            if (c == '_' || c == '-') {
                upper = true;
            } else if (upper) {
                result.append(Character.toUpperCase(c));
                upper = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    private String toPascal(String value) {
        String camel = toCamel(value);
        return camel.isEmpty() ? camel : Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
    }

    private String toKebab(String value) {
        return value.toLowerCase(Locale.ROOT).replace('_', '-');
    }

    private String lowerFirst(String value) {
        return value == null || value.isEmpty() ? value : Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    private String toJavaPackageSegment(String value) {
        String source = StringUtils.hasText(value) ? value : "generated";
        String segment = source.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "");
        if (!StringUtils.hasText(segment)) {
            return "generated";
        }
        if (!Character.isJavaIdentifierStart(segment.charAt(0))) {
            return "x" + segment;
        }
        return segment;
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private String required(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private record CodegenTemplate(String name, String path, String templatePath) {
    }
}
