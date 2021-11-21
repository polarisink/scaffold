package com.lqs.scaffold.service;

import com.lqs.scaffold.entity.Books;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 书 服务类
 * </p>
 *
 * @author polaris
 * @since 2021-11-10
 */
public interface BookService extends IService<Books> {
	/**
	 * 获取所有书
	 *
	 * @return
	 */
	public List<Books> getAllBooks();

	public Books getBookById(Long id);

	public Integer addBook(Books book);

	public Books updateBook(Books book);

	public Integer deleteById(Long id);

	public Integer testRabbitMq();
}
