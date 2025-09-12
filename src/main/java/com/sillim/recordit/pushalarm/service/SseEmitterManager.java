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
	private final ConcurrentHashMap<Long, SseEmitter> clients = new ConcurrentHashMap<>();

	public SseEmitter subscribe(Long memberId) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		clients.put(memberId, emitter);

		emitter.onCompletion(() -> clients.remove(memberId));
		emitter.onTimeout(() -> clients.remove(memberId));

		log.info("[SSE SUBSCRIBE] {}", memberId);

		return emitter;
	}

	public boolean sendToClient(Long memberId, PushMessage message) throws IOException {
		SseEmitter emitter = clients.get(memberId);

		if (emitter != null) {
			emitter.send(SseEmitter.event().name(message.type().name()).data(message));
			return true;
		}
		return false;
	}
}
