package com.lqs.scaffold.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lqs
 * @describe
 * @date 2021/11/21
 */
@Configuration
public class RabbitMqConfig {

	@Value("${spring.rabbitmq.host}")
	private String host;
	@Value("${spring.rabbitmq.port}")
	private int port;
	@Value("${spring.rabbitmq.username}")
	private String username;
	@Value("${spring.rabbitmq.password}")
	private String password;
	@Value("${spring.rabbitmq.virtual-host}")
	private String virtualhost;
	public static final String QUEUE_NAME = "spring-boot-queue";
	public static final String EXCHANGE_NAME = "spring-boot-exchange";
	public static final String ROUTING_KEY = "spring-boot-key";

	/**
	 * 创建队列
	 *
	 * @return
	 */
	@Bean
	public Queue queue() {
		return new Queue(QUEUE_NAME);
	}

	/**
	 * 创建一个 topic 类型的交换器
	 *
	 * @return
	 */
	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(EXCHANGE_NAME);
	}

	/**
	 * 使用路由键（routingKey）把队列（Queue）绑定到交换器（Exchange）
	 *
	 * @param queue
	 * @param exchange
	 * @return
	 */
	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setVirtualHost(virtualhost);
		return connectionFactory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}

}

