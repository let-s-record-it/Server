package com.sillim.recordit.pushalarm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;
import com.sillim.recordit.pushalarm.domain.AlarmLog;
import com.sillim.recordit.pushalarm.dto.PushMessage;
import com.sillim.recordit.pushalarm.repository.AlarmLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {
	private final SseEmitterManager sseEmitterManager;
	private final AlarmLogRepository alarmLogRepository;
	private final ObjectMapper objectMapper;

	public <T> void pushAlarm(Long senderId, Long receiverId, PushMessage<T> message) {
		try {
			AlarmLog alarmLog =
					alarmLogRepository.save(
							AlarmLog.builder()
									.alarmType(message.type())
									.content(objectMapper.writeValueAsString(message.content()))
									.senderId(senderId)
									.receiverId(receiverId)
									.build());
			sseEmitterManager.sendToClient(
					receiverId,
					new PushMessage<>(alarmLog.getId(), message.type(), message.content()));
		} catch (JsonProcessingException e) {
			throw new ApplicationException(
					ErrorCode.UNHANDLED_EXCEPTION, "AlarmLog content를 직렬화하지 못했습니다.");
		}
	}

	public void deleteAlarm(Long alarmLogId) {
		alarmLogRepository.deleteById(alarmLogId);
	}
}
