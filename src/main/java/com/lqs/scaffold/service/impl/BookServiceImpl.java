package com.lqs.scaffold.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lqs.scaffold.entity.Books;
import com.lqs.scaffold.dao.BookDao;
import com.lqs.scaffold.service.BookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 书 服务实现类
 * </p>
 *
 * @author polaris
 * @since 2021-11-10
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookDao, Books> implements BookService {
	private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);
	private final BookDao bookDao;

	public BookServiceImpl(BookDao bookDao) {
		this.bookDao = bookDao;
	}


	/**
	 * 一般用在查询方法上,缓存方法返回值
	 * key可以使用SpEL表达式
	 * key和keyGenerator一般不同时使用
	 * condition:满足一定条件才会进行缓存
	 * unless:满足条件就不缓存
	 *
	 * @param id
	 * @return
	 */
	@Cacheable(key = "#id", value = "getBookById", condition = "#id>0", unless = "#id<0")
	@Override
	public Books getBookById(Long id) {
		return bookDao.selectById(id);
	}


	/**
	 * 一般用于更新方法,会缓存方法返回值
	 *
	 * @param book
	 * @return
	 */
	@CachePut(key = "#book.id", value = "updateBookById")
	@Override
	public Books updateBook(Books book) {
		int id = bookDao.updateById(book);
		return bookDao.selectById(id);
	}

	/**
	 * 用于数据库删除记录时,删除缓存
	 *
	 * @param id
	 * @return
	 */
	@CacheEvict(key = "#id", value = "deleteById")
	@Override
	public Integer deleteById(Long id) {
		return bookDao.deleteById(id);
	}


	@Override
	public Integer addBook(Books book) {
		return bookDao.insert(book);
	}


	@Cacheable(value = "allBooks")
	@Override
	public List<Books> getAllBooks() {
		return bookDao.selectList(null);
	}


}
