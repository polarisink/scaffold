package com.lqs.scaffold.message;

import cn.hutool.core.lang.Console;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lqs.scaffold.config.RabbitMqConfig;
import com.lqs.scaffold.entity.Books;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lqs
 * @describe
 * @date 2021/11/21
 */
@Component
public class Consumer {
	private static final Logger log = LoggerFactory.getLogger(Consumer.class);

	private final ObjectMapper objectMapper;

	public Consumer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * 测试消费者
	 *
	 * @param message
	 */
	@RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
	public void consumeMessage(@Payload byte[] message) {
		Books books = null;
		try {
			books = objectMapper.readValue(message, Books.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("bookName:{} ,bookCount:{}", books.getName(), books.getCount());
	}
}
