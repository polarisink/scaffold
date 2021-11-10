package com.lqs.scaffold.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lqs.scaffold.entity.User;
import com.lqs.scaffold.dao.UserDao;
import com.lqs.scaffold.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author polaris
 * @since 2021-11-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
  private final UserDao userDao;

  public UserServiceImpl(UserDao userDao){
    this. userDao= userDao;
  }
}
