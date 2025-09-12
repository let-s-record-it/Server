package com.sillim.recordit.pushalarm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.pushalarm.domain.AlarmLog;
import com.sillim.recordit.pushalarm.dto.AlarmType;
import com.sillim.recordit.pushalarm.dto.PushMessage;
import com.sillim.recordit.pushalarm.repository.AlarmLogRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class AlarmLogServiceTest {

	@Mock AlarmLogRepository alarmLogRepository;
	@InjectMocks AlarmLogService alarmLogService;

	@Test
	@DisplayName("최근에 생성된 순으로 AlarmLog를 가져온다.")
	void searchRecentCreated() {
		long memberId = 1L;
		PageRequest pageable = PageRequest.of(0, 1);
		AlarmLog alarmLog = new AlarmLog(1L, AlarmType.FOLLOWING, "title", "body", 1L, 1L);
		SliceImpl<AlarmLog> slice = new SliceImpl<>(List.of(alarmLog), pageable, false);
		given(
						alarmLogRepository.findByDeletedIsFalseAndReceiverIdOrderByCreatedAtDesc(
								eq(pageable), eq(memberId)))
				.willReturn(slice);

		SliceResponse<PushMessage> response =
				alarmLogService.searchRecentCreated(pageable, memberId);

		assertThat(response.content()).hasSize(1);
	}

	@Test
	@DisplayName("알람을 삭제한다.")
	void deleteAlarmLog() {
		long alarmLogId = 1L;
		long memberId = 1L;
		AlarmLog alarmLog = spy(new AlarmLog(1L, AlarmType.FOLLOWING, "title", "body", 1L, 1L));
		given(alarmLogRepository.findById(eq(alarmLogId))).willReturn(Optional.of(alarmLog));

		alarmLogService.deleteAlarmLog(alarmLogId, memberId);

		verify(alarmLog, times(1)).delete();
	}

	@Test
	@DisplayName("알람 받은 멤버가 아니면 알람 삭제 시 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionIfNotReceiverWhenDeleteAlarmLog() {
		long alarmLogId = 1L;
		long memberId = 1L;
		AlarmLog alarmLog = new AlarmLog(1L, AlarmType.FOLLOWING, "title", "body", 1L, 2L);
		given(alarmLogRepository.findById(eq(alarmLogId))).willReturn(Optional.of(alarmLog));

		assertThatCode(() -> alarmLogService.deleteAlarmLog(alarmLogId, memberId))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}
}
