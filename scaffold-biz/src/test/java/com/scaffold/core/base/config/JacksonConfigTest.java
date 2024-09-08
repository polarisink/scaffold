package com.scaffold.core.base.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import org.junit.jupiter.api.Test;

import java.util.Map;

class JacksonConfigTest {

    @Test
    void test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultTyping(new StdTypeResolverBuilder().init(JsonTypeInfo.Id.CLASS, null).inclusion(JsonTypeInfo.As.PROPERTY));
        String json = mapper.writeValueAsString(new B(new T("1", "1"), 1, Map.of("key", 1)));
        B b = mapper.readValue(json, B.class);
        System.out.println(json);
        System.out.println(b);
    }

    @Test
    void jsonNode() {
//        ObjectNode root = JsonUtil.getMapper().createObjectNode();
//        root.put("key", 1);
//        root.put("value", 1);
//        ObjectNode node = JsonUtil.getMapper().createObjectNode();
//        node.put("key", 1);
//        node.put("value", 1);
//        root.set("child", node);
//        System.out.println(root);
    }

    record T(String a, String b) {
    }

    record B(T t, int age, Map<String, Integer> map) {
    }
}