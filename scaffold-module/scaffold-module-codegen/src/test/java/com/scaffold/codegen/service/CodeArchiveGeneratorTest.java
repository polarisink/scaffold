package com.scaffold.codegen.service;

import com.scaffold.codegen.model.GenColumn;
import com.scaffold.codegen.model.GenTable;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

class CodeArchiveGeneratorTest {

    @Test
    void shouldGenerateBackendAndNaiveFrontendSources() throws Exception {
        GenTable table = new GenTable();
        table.setTableName("sys_demo");
        table.setClassName("Demo");
        table.setModuleName("system");
        table.setBusinessName("demo");
        table.setPackageName("com.scaffold.system");
        table.setAuthor("scaffold");
        table.setBackendPath("scaffold-biz/src/main/java");
        table.setFrontendPath("vue-vben-admin/apps/web-naive/src");
        table.setMenuName("示例管理");
        table.setMenuPath("/system/demo");
        table.setPermissionPrefix("system:demo");
        table.setDefaultRoleCode("admin");

        GenColumn id = new GenColumn();
        id.setColumnName("id");
        id.setPropertyName("id");
        id.setJavaType("Long");
        id.setTsType("number");
        id.setPrimaryKey(true);
        id.setQueryable(false);
        id.setFormVisible(false);
        id.setListVisible(true);
        id.setNullable(false);
        id.setAutoIncrement(true);
        id.setUniqueKey(false);
        id.setQueryType("EQ");
        id.setFormWidget("Input");
        id.setColumnComment("主键");
        table.setColumns(java.util.List.of(id));

        byte[] archive = new CodeArchiveGenerator().generate(table);
        Map<String, String> entries = unzip(archive);

        assertThat(entries).containsKeys(
                "scaffold-biz/src/main/java/com/scaffold/system/demo/Demo.java",
                "scaffold-biz/src/main/java/com/scaffold/system/demo/DemoController.java",
                "vue-vben-admin/apps/web-naive/src/api/system/demo.ts",
                "vue-vben-admin/apps/web-naive/src/views/system/demo/index.vue");
        assertThat(entries.get("scaffold-biz/src/main/java/com/scaffold/system/demo/Demo.java"))
                .contains("class Demo");
    }

    private Map<String, String> unzip(byte[] archive) throws Exception {
        Map<String, String> entries = new HashMap<>();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(archive), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                entries.put(entry.getName(), new String(zip.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
        return entries;
    }
}
