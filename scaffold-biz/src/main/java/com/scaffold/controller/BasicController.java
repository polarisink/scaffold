package com.scaffold.controller;

import com.scaffold.base.util.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/basic")
public class BasicController {
    @GetMapping
    public R<String> b(){
        return R.success("134");
    }
}
