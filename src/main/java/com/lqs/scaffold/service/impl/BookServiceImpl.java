package com.lqs.scaffold.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lqs.scaffold.entity.Book;
import com.lqs.scaffold.dao.BookDao;
import com.lqs.scaffold.service.BookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 书 服务实现类
 * </p>
 *
 * @author polaris
 * @since 2021-11-10
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookDao, Book> implements BookService {
  private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);
  private final BookDao bookDao;

  public BookServiceImpl(BookDao bookDao){
    this. bookDao= bookDao;
  }
}
