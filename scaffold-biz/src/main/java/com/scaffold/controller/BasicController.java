package com.scaffold.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "基础")
@RestController
@RequestMapping("/basic")
public class BasicController {

    @GetMapping("/version")
    public String version() {
        return "1.0";
    }
}
