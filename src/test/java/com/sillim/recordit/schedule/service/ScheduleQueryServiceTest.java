package com.sillim.recordit.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
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
	@InjectMocks ScheduleQueryService scheduleQueryService;

	@Test
	@DisplayName("schedule id를 통해 일정을 조회할 수 있다.")
	void searchScheduleByScheduleId() {
		Schedule expectedSchedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDatetime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDatetime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.colorHex("aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.alarmTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.scheduleGroup(new ScheduleGroup(false))
						.build();
		given(scheduleRepository.findById(anyLong())).willReturn(Optional.of(expectedSchedule));

		Schedule schedule = scheduleQueryService.searchSchedule(1L);

		assertThat(schedule).isEqualTo(expectedSchedule);
	}

	@Test
	@DisplayName("특정 달의 일정을 조회할 수 있다.")
	void searchScheduleInMonth() {
		Schedule expectedSchedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDatetime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDatetime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.colorHex("aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.alarmTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.scheduleGroup(new ScheduleGroup(false))
						.build();
		given(scheduleRepository.findScheduleInMonth(anyLong(), eq(2024), eq(1)))
				.willReturn(List.of(expectedSchedule));

		List<Schedule> schedules = scheduleQueryService.searchSchedulesInMonth(1L, 2024, 1);

		assertThat(schedules).hasSize(1);
		assertThat(schedules.get(0)).isEqualTo(expectedSchedule);
	}
}
