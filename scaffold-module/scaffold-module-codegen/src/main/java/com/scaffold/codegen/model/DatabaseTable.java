package com.scaffold.codegen.model;

public record DatabaseTable(String catalog, String schema, String name, String comment, String type) {
}
