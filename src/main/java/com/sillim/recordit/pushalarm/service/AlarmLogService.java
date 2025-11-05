package com.sillim.recordit.pushalarm.service;

import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.pushalarm.domain.AlarmLog;
import com.sillim.recordit.pushalarm.dto.PushMessage;
import com.sillim.recordit.pushalarm.repository.AlarmLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmLogService {
	private final AlarmLogRepository alarmLogRepository;

	public SliceResponse<PushMessage<String>> searchRecentCreated(
			Pageable pageable, Long memberId) {
		Slice<AlarmLog> alarmSlice =
				alarmLogRepository.findByDeletedIsFalseAndReceiverIdOrderByCreatedAtDesc(
						pageable, memberId);

		return SliceResponse.of(
				new SliceImpl<>(
						alarmSlice.stream()
								.map(
										alarmLog ->
												new PushMessage<>(
														alarmLog.getId(),
														alarmLog.getAlarmType(),
														alarmLog.getContent()))
								.toList(),
						pageable,
						alarmSlice.hasNext()));
	}

	@Transactional
	public void deleteAlarmLog(Long alarmLogId, Long memberId) {
		AlarmLog alarmLog =
				alarmLogRepository
						.findById(alarmLogId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.ALARM_LOG_NOT_FOUND));

		if (!alarmLog.isReceiver(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}

		alarmLog.delete();
	}
}
