package com.scaffold.codegen.service;

import com.scaffold.codegen.model.GenColumn;
import com.scaffold.codegen.model.GenTable;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Renders one configured table into a complete source archive. */
@Component
public class CodeArchiveGenerator {

    private final Configuration freemarker;

    public CodeArchiveGenerator() {
        freemarker = new Configuration(Configuration.VERSION_2_3_32);
        freemarker.setDefaultEncoding(StandardCharsets.UTF_8.name());
        freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarker.setLogTemplateExceptions(false);
        freemarker.setWrapUncheckedExceptions(true);
    }

    public byte[] generate(GenTable table) {
        Map<String, Object> model = buildModel(table);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(output, StandardCharsets.UTF_8)) {
            for (CodegenTemplate item : templates()) {
                zip.putNextEntry(new ZipEntry(renderString("path-" + item.name(), item.path(), model)));
                zip.write(renderTemplate(item.templatePath(), model).getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
            zip.finish();
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("生成 ZIP 失败", e);
        }
    }

    private Map<String, Object> buildModel(GenTable table) {
        GenColumn pk = table.getColumns().stream().filter(c -> Boolean.TRUE.equals(c.getPrimaryKey())).findFirst()
                .orElseGet(() -> table.getColumns().isEmpty() ? null : table.getColumns().getFirst());
        List<GenColumn> businessColumns = table.getColumns().stream().filter(this::isBusinessColumn).toList();
        List<GenColumn> uniqueColumns = businessColumns.stream()
                .filter(column -> Boolean.TRUE.equals(column.getUniqueKey())).toList();

        Map<String, Object> model = new HashMap<>();
        model.put("table", table);
        model.put("columns", table.getColumns());
        model.put("businessColumns", businessColumns);
        model.put("uniqueColumns", uniqueColumns);
        model.put("hasUniqueColumns", !uniqueColumns.isEmpty());
        model.put("pk", pk);
        model.put("imports", imports(table.getColumns(), column -> true));
        model.put("entityImports", imports(businessColumns, column -> true));
        model.put("queryImports", imports(table.getColumns(), column -> Boolean.TRUE.equals(column.getQueryable())));
        model.put("createImports", imports(table.getColumns(), column -> Boolean.TRUE.equals(column.getFormVisible())));
        model.put("packagePath", table.getPackageName().replace('.', '/'));
        model.put("className", table.getClassName());
        model.put("lowerClassName", lowerFirst(table.getClassName()));
        model.put("moduleName", table.getModuleName());
        model.put("businessName", table.getBusinessName());
        model.put("javaBusinessName", javaPackageSegment(table.getBusinessName()));
        model.put("apiPrefix", "/" + table.getModuleName() + "/" + table.getBusinessName());
        model.put("author", table.getAuthor());
        model.put("now", LocalDateTime.now().toString());
        return model;
    }

    private Set<String> imports(List<GenColumn> columns, java.util.function.Predicate<GenColumn> included) {
        Set<String> imports = new TreeSet<>();
        columns.stream().filter(included).map(GenColumn::getJavaType)
                .filter(type -> type != null && type.contains("."))
                .forEach(imports::add);
        return imports;
    }

    private boolean isBusinessColumn(GenColumn column) {
        return !Set.of("id", "gmt_modified", "gmt_created", "created_by", "modified_by", "deleted")
                .contains(column.getColumnName())
                && !Set.of("id", "gmtModified", "gmtCreated", "createdBy", "modifiedBy", "deleted")
                .contains(column.getPropertyName());
    }

    private List<CodegenTemplate> templates() {
        List<CodegenTemplate> items = new ArrayList<>();
        String javaBase = "${table.backendPath}/${packagePath}/${javaBusinessName}";
        items.add(new CodegenTemplate("entity", javaBase + "/${className}.java", "codegen/templates/entity.java.ftl"));
        items.add(new CodegenTemplate("query", javaBase + "/${className}QueryDTO.java", "codegen/templates/query-dto.java.ftl"));
        items.add(new CodegenTemplate("create", javaBase + "/${className}CreateDTO.java", "codegen/templates/create-dto.java.ftl"));
        items.add(new CodegenTemplate("mapper", javaBase + "/${className}Mapper.java", "codegen/templates/mapper.java.ftl"));
        items.add(new CodegenTemplate("service", javaBase + "/${className}Service.java", "codegen/templates/service.java.ftl"));
        items.add(new CodegenTemplate("controller", javaBase + "/${className}Controller.java", "codegen/templates/controller.java.ftl"));
        String frontBase = "${table.frontendPath}/views/${moduleName}/${businessName}";
        items.add(new CodegenTemplate("frontend-api", "${table.frontendPath}/api/${moduleName}/${businessName}.ts", "codegen/templates/frontend-api.ts.ftl"));
        items.add(new CodegenTemplate("frontend-type", "${table.frontendPath}/api/${moduleName}/${businessName}.model.ts", "codegen/templates/frontend-type.ts.ftl"));
        items.add(new CodegenTemplate("frontend-page", frontBase + "/index.vue", "codegen/templates/frontend-page-naive.vue.ftl"));
        items.add(new CodegenTemplate("frontend-page-ele", frontBase + "/index.ele.vue", "codegen/templates/frontend-page-ele.vue.ftl"));
        items.add(new CodegenTemplate("frontend-page-antd", frontBase + "/index.antd.vue", "codegen/templates/frontend-page-antd.vue.ftl"));
        return items;
    }

    private String renderString(String name, String source, Map<String, Object> model) throws Exception {
        Template template = new Template(name, new StringReader(source), freemarker);
        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

    private String renderTemplate(String templatePath, Map<String, Object> model) throws Exception {
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

    private String lowerFirst(String value) {
        return value == null || value.isEmpty() ? value : Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    private String javaPackageSegment(String value) {
        String source = StringUtils.hasText(value) ? value : "generated";
        String segment = source.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9_]", "");
        if (!StringUtils.hasText(segment)) return "generated";
        return Character.isJavaIdentifierStart(segment.charAt(0)) ? segment : "x" + segment;
    }

    private record CodegenTemplate(String name, String path, String templatePath) { }
}
