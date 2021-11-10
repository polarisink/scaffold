package com.lqs.scaffold.controller;


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
public class BookController {
  private static final Logger log = LoggerFactory.getLogger(BookController.class);
  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }
}
