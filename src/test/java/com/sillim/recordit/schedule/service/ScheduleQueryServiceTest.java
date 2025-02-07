package com.sillim.recordit.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.response.DayScheduleResponse;
import com.sillim.recordit.schedule.dto.response.MonthScheduleResponse;
import com.sillim.recordit.schedule.fixture.RepetitionPatternFixture;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleQueryServiceTest {

	@Mock ScheduleRepository scheduleRepository;
	@Mock CalendarQueryService calendarQueryService;
	@Mock RepetitionPatternService repetitionPatternService;
	@InjectMocks ScheduleQueryService scheduleQueryService;

	@Test
	@DisplayName("schedule id를 통해 일정을 조회할 수 있다.")
	void searchScheduleByScheduleId() {
		long memberId = 1L;
		Calendar calendar = mock(Calendar.class);
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		Schedule expectedSchedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDateTime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.colorHex("aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.scheduleGroup(scheduleGroup)
						.calendar(calendar)
						.scheduleAlarms(List.of(LocalDateTime.of(2024, 1, 1, 0, 0)))
						.build();
		given(calendar.isOwnedBy(eq(memberId))).willReturn(true);
		given(scheduleRepository.findByScheduleId(anyLong()))
				.willReturn(Optional.of(expectedSchedule));

		DayScheduleResponse dayScheduleResponse = scheduleQueryService.searchSchedule(1L, memberId);

		assertThat(dayScheduleResponse.title()).isEqualTo(expectedSchedule.getTitle());
		assertThat(dayScheduleResponse.description()).isEqualTo(expectedSchedule.getDescription());
	}

	@Test
	@DisplayName("schedule id를 통해 반복된 일정을 조회할 수 있다.")
	void searchRepeatedScheduleByScheduleId() {
		long memberId = 1L;
		Calendar calendar = mock(Calendar.class);
		ScheduleGroup scheduleGroup = new ScheduleGroup(true);
		Schedule expectedSchedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDateTime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.colorHex("aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.scheduleGroup(scheduleGroup)
						.calendar(calendar)
						.scheduleAlarms(List.of(LocalDateTime.of(2024, 1, 1, 0, 0)))
						.build();
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.WEEKLY.getRepetitionPattern(scheduleGroup);
		given(calendar.isOwnedBy(eq(memberId))).willReturn(true);
		given(scheduleRepository.findByScheduleId(anyLong()))
				.willReturn(Optional.of(expectedSchedule));
		given(repetitionPatternService.searchByScheduleGroupId(any()))
				.willReturn(repetitionPattern);

		DayScheduleResponse dayScheduleResponse = scheduleQueryService.searchSchedule(1L, memberId);

		assertThat(dayScheduleResponse.title()).isEqualTo(expectedSchedule.getTitle());
		assertThat(dayScheduleResponse.description()).isEqualTo(expectedSchedule.getDescription());
	}

	@Test
	@DisplayName("일정 조회 시 member가 해당 일정의 소유자가 아니면 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionIfMemberIsNotOwnerForSchedule() {
		long memberId = 1L;
		Calendar calendar = mock(Calendar.class);
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		Schedule expectedSchedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDateTime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.colorHex("aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.scheduleGroup(scheduleGroup)
						.calendar(calendar)
						.scheduleAlarms(List.of(LocalDateTime.of(2024, 1, 1, 0, 0)))
						.build();
		given(calendar.isOwnedBy(anyLong())).willReturn(false);
		given(scheduleRepository.findByScheduleId(anyLong()))
				.willReturn(Optional.of(expectedSchedule));

		assertThatCode(() -> scheduleQueryService.searchSchedule(1L, 2L))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}

	@Test
	@DisplayName("특정 달의 일정을 조회할 수 있다.")
	void searchScheduleInMonth() {
		long memberId = 1L;
		Calendar calendar = mock(Calendar.class);
		Schedule expectedSchedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDateTime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.colorHex("aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.scheduleGroup(new ScheduleGroup(false))
						.calendar(calendar)
						.scheduleAlarms(List.of(LocalDateTime.of(2024, 1, 1, 0, 0)))
						.build();
		given(calendarQueryService.searchByCalendarId(anyLong())).willReturn(calendar);
		given(scheduleRepository.findScheduleInMonth(anyLong(), eq(2024), eq(1)))
				.willReturn(List.of(expectedSchedule));

		List<MonthScheduleResponse> monthScheduleResponses =
				scheduleQueryService.searchSchedulesInMonth(1L, 2024, 1, memberId);

		assertThat(monthScheduleResponses).hasSize(1);
		assertThat(monthScheduleResponses.get(0).title()).isEqualTo(expectedSchedule.getTitle());
	}

	@Test
	@DisplayName("특정 일의 일정을 조회할 수 있다.")
	void searchScheduleInDay() {
		long memberId = 1L;
		Calendar calendar = mock(Calendar.class);
		Schedule expectedSchedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDateTime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.colorHex("aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.scheduleGroup(new ScheduleGroup(false))
						.calendar(calendar)
						.scheduleAlarms(List.of(LocalDateTime.of(2024, 1, 1, 0, 0)))
						.build();
		given(calendarQueryService.searchByCalendarId(anyLong())).willReturn(calendar);
		given(scheduleRepository.findScheduleInDay(anyLong(), eq(LocalDate.of(2024, 1, 15))))
				.willReturn(List.of(expectedSchedule));

		List<DayScheduleResponse> dayScheduleResponses =
				scheduleQueryService.searchSchedulesInDay(1L, LocalDate.of(2024, 1, 15), memberId);

		assertThat(dayScheduleResponses).hasSize(1);
		assertThat(dayScheduleResponses.get(0).title()).isEqualTo(expectedSchedule.getTitle());
	}
}
