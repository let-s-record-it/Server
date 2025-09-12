package com.sillim.recordit.pushalarm.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.pushalarm.dto.AlarmType;
import com.sillim.recordit.pushalarm.dto.PushMessage;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SseEmitterManagerTest {

	private final SseEmitterManager sseEmitterManager = new SseEmitterManager();

	@Test
	@DisplayName("sseEmitter로 client에게 message를 보낼 수 있다.")
	void sendToClient() throws IOException {
		long memberId = 1L;
		sseEmitterManager.subscribe(memberId);

		boolean result =
				sseEmitterManager.sendToClient(
						memberId, new PushMessage(1L, AlarmType.INVITE, 1L, "title", "body"));

		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("subscribe한 client가 없으면 message를 보낼 수 없다.")
	void cantSendToClientIfNotExistsClientSubscribeSseEmitter() throws IOException {
		long memberId = 1L;

		boolean result =
				sseEmitterManager.sendToClient(
						memberId, new PushMessage(1L, AlarmType.INVITE, 1L, "title", "body"));

		assertThat(result).isFalse();
	}
}
