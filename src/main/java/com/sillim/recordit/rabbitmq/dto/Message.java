package com.sillim.recordit.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.ToString;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ToString
public class Message<T> {

	private String title;
	private T content;
	private MessageType type;

	public Message(String title, T content, MessageType type) {
		this.title = title;
		this.content = content;
		this.type = type;
	}
}
