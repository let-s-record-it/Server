package com.sillim.recordit.pushalarm.service;

import com.sillim.recordit.pushalarm.domain.AlarmLog;
import com.sillim.recordit.pushalarm.dto.PushMessage;
import com.sillim.recordit.pushalarm.repository.AlarmLogRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {
	private final SseEmitterManager sseEmitterManager;
	private final AlarmLogRepository alarmLogRepository;

	public boolean pushAlarm(Long senderId, Long receiverId, PushMessage message)
			throws IOException {
		AlarmLog alarmLog =
				alarmLogRepository.save(
						AlarmLog.builder()
								.activeId(message.activeId())
								.alarmType(message.type())
								.title(message.title())
								.body(message.body())
								.senderId(senderId)
								.receiverId(receiverId)
								.build());

		return sseEmitterManager.sendToClient(receiverId, PushMessage.fromAlarmLog(alarmLog));
	}

	public void deleteAlarm(Long alarmLogId) {
		alarmLogRepository.deleteById(alarmLogId);
	}
}
