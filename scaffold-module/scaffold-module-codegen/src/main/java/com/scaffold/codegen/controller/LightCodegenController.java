package com.scaffold.codegen.controller;

import com.scaffold.base.util.R;
import com.scaffold.codegen.model.DatabaseTable;
import com.scaffold.codegen.model.GenTable;
import com.scaffold.codegen.service.LightCodegenService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/codegen")
@RequiredArgsConstructor
public class LightCodegenController {

    private final LightCodegenService codegenService;

    @GetMapping({"/configs", "/tables"})
    public List<GenTable> listConfigs() {
        return codegenService.listTables();
    }

    @GetMapping({"/configs/{id}", "/tables/{id}"})
    public GenTable getConfig(@PathVariable Long id) {
        return codegenService.getTable(id);
    }

    @PostMapping({"/configs", "/tables"})
    public R<Long> create(@RequestBody GenTable table) {
        return R.success(codegenService.create(table));
    }

    @PutMapping({"/configs/{id}", "/tables/{id}"})
    public void update(@PathVariable Long id, @RequestBody GenTable table) {
        codegenService.update(id, table);
    }

    @DeleteMapping({"/configs/{id}", "/tables/{id}"})
    public void delete(@PathVariable Long id) {
        codegenService.delete(id);
    }

    @GetMapping("/database/tables")
    public List<DatabaseTable> listDatabaseTables(@RequestParam(required = false) String name) {
        return codegenService.listDatabaseTables(name);
    }

    @PostMapping("/database/import")
    public R<Long> importTable(@RequestParam String tableName, @RequestParam(required = false) String schema) {
        return R.success(codegenService.importTable(tableName, schema));
    }

    @GetMapping({"/configs/{id}/download", "/tables/{id}/download"})
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) {
        GenTable table = codegenService.getTable(id);
        byte[] bytes = codegenService.download(id);
        String filename = (table.getBusinessName() == null ? table.getTableName() : table.getBusinessName()) + "-codegen.zip";
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(bytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(new ByteArrayResource(bytes));
    }
}
