package com.lqs.scaffold.service.impl;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lqs.scaffold.config.RabbitMqConfig;
import com.lqs.scaffold.property.MqProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lqs.scaffold.entity.Books;
import com.lqs.scaffold.dao.BookDao;
import com.lqs.scaffold.service.BookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;

	public BookServiceImpl(BookDao bookDao, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
		this.bookDao = bookDao;
		this.rabbitTemplate = rabbitTemplate;
		this.objectMapper = objectMapper;
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
	public Integer testRabbitMq() {
		new Thread(() -> producer()).start();
		return 1;
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


	private void producer()  {
		for (int i = 0; i < 100; i++) {
			String value = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
			Books books = new Books();
			books.setId(System.currentTimeMillis());
			books.setName("rabbitmq learn");
			books.setCount(100);
			byte[] valueAsString = null;
			try {
				valueAsString = objectMapper.writeValueAsBytes(value);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			Message message = MessageBuilder.withBody(valueAsString).build();
			log.info("send message {}", valueAsString);
			rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUTING_KEY, message);
		}
	}
}
