package com.sillim.recordit.rabbitmq.service;

import com.sillim.recordit.rabbitmq.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePublisher {

	@Value("${rabbitmq.exchange.name}")
	private String exchangeName;

	@Value("${rabbitmq.routing.key}")
	private String routingKey;

	private final RabbitTemplate rabbitTemplate;

	public <T> void send(Message<T> message) {
		log.info("[RabbitMQ] 메세지 전송: {}", message.toString());
		rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
	}
}
