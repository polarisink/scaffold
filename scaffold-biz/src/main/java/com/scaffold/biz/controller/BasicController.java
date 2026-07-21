package com.scaffold.biz.controller;

import com.scaffold.biz.entity.Order;
import com.scaffold.biz.mapper.OrderMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@Tag(name = "基础")
@RestController
@RequestMapping("/basic")
@RequiredArgsConstructor
public class BasicController {

    private final OrderMapper orderMapper;

    @GetMapping("/version")
    public String version() {
        return "1.0";
    }

    @Cacheable(cacheNames = "ORDER",key = "#name")
    @GetMapping("/order/{name}")
    public Order select(@PathVariable String name) {
        log.info("now:{}", LocalDateTime.now());
        return orderMapper.selectByOrderName(name);
    }
}
