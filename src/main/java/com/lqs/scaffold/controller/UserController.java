package com.lqs.scaffold.controller;


import com.lqs.scaffold.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author polaris
 * @since 2021-11-10
 */
@RestController
@RequestMapping("/scaffold/user")
public class UserController {
  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }
}
