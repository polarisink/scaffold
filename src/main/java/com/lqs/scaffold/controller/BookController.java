package com.lqs.scaffold.controller;


import com.lqs.scaffold.core.ResultT;
import com.lqs.scaffold.exception.BadRequestException;
import com.lqs.scaffold.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 书 前端控制器
 * </p>
 *
 * @author polaris
 * @since 2021-11-10
 */
@RestController
@RequestMapping("/scaffold/book")
@Tag(name = "书相关API")
public class BookController {
	private static final Logger log = LoggerFactory.getLogger(BookController.class);
	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@Operation(summary = "获取所有书")
	@GetMapping("/all")
	public ResultT getAllBooks() throws BadRequestException {
		return ResultT.success(bookService.getAllBooks());
	}
}
