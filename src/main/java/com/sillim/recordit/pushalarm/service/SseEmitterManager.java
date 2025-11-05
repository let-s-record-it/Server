package com.sillim.recordit.pushalarm.service;

import com.sillim.recordit.pushalarm.dto.PushMessage;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
public class SseEmitterManager {

	private static final Long TIMEOUT = 60L * 60 * 1000;

	private final ConcurrentHashMap<Long, SseEmitter> clients = new ConcurrentHashMap<>();

	public SseEmitter subscribe(Long memberId) {
		SseEmitter oldEmitter = clients.get(memberId);
		if (oldEmitter != null) {
			log.info("[SSE CLEANUP] Old Connection 삭제, Member: {}", memberId);
			oldEmitter.complete();
			clients.remove(memberId);
		}

		SseEmitter emitter = new SseEmitter(TIMEOUT);
		clients.put(memberId, emitter);

		emitter.onCompletion(
				() -> {
					log.info("[SSE COMPLETE] Member: {}", memberId);
					clients.remove(memberId);
				});
		emitter.onTimeout(
				() -> {
					log.info("[SSE TIMEOUT] Member: {}", memberId);
					clients.remove(memberId);
				});
		emitter.onError(
				throwable -> {
					log.info("[SSE ERROR] Member: {}, Error: {}", memberId, throwable.getMessage());
					clients.remove(memberId);
				});

		log.info("[SSE SUBSCRIBE] {}", memberId);

		return emitter;
	}

	public <T> boolean sendToClient(Long memberId, PushMessage<T> message) {
		SseEmitter emitter = clients.get(memberId);

		if (emitter == null) {
			log.warn("[SSE NOT FOUND] Member: {}", memberId);
			return false;
		}

		log.info("[SSE SEND] {} {}", memberId, emitter);
		try {
			emitter.send(SseEmitter.event().name(message.type().name()).data(message));
			return true;
		} catch (IOException exception) {
			clients.remove(memberId);
			emitter.completeWithError(exception);
			return false;
		}
	}
}
