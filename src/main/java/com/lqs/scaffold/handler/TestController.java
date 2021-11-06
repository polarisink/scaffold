package com.lqs.scaffold.handler;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lqs
 * @describe
 * @date 2021/11/5
 */

@Api("测试Controller")
@RestController
@RequestMapping("/test")
public class TestController {

	@ApiOperation("hello world")
	@GetMapping("/world")
	public String world() {
		return "world";
	}

	@GetMapping("/printName")
	public String test(String name) {
		return name;
	}
}
