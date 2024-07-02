package com.sillim.recordit.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.domain.vo.ScheduleDuration;
import com.sillim.recordit.schedule.dto.request.RepetitionAddRequest;
import com.sillim.recordit.schedule.dto.request.ScheduleAddRequest;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleCommandServiceTest {

	@Mock ScheduleRepository scheduleRepository;
	@Mock CalendarService calendarService;
	@Mock ScheduleGroupService scheduleGroupService;
	@Mock RepetitionPatternService repetitionPatternService;
	@InjectMocks ScheduleCommandService scheduleCommandService;

	Member member;
	Calendar calendar;
	long calendarId = 1L;

	@BeforeEach
	void initObjects() {
		member =
				Member.builder()
						.auth(new Auth("1234567", OAuthProvider.KAKAO))
						.name("name")
						.job("job")
						.deleted(false)
						.memberRole(List.of(MemberRole.ROLE_USER))
						.build();
		calendar = CalendarFixture.DEFAULT.getCalendar(member);
	}

	@Test
	@DisplayName("반복 없는 schedule을 추가할 수 있다.")
	void addSchedule() {
		ScheduleAddRequest scheduleAddRequest =
				new ScheduleAddRequest(
						"title",
						"description",
						false,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0),
						false,
						null,
						"aaffbb",
						"서울역",
						true,
						36.0,
						127.0,
						true,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)));
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		Schedule schedule =
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
						.calendar(calendar)
						.scheduleGroup(scheduleGroup)
						.scheduleAlarms(List.of(LocalDateTime.of(2024, 1, 1, 0, 0)))
						.build();
		given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);

		List<Schedule> schedules =
				scheduleCommandService.addSchedules(scheduleAddRequest, calendarId);

		assertAll(
				() -> {
					assertThat(schedules).hasSize(1);
					assertThat(schedules.get(0).getTitle()).isEqualTo("title");
					assertThat(schedules.get(0).getDescription()).isEqualTo("description");
					assertThat(schedules.get(0).getScheduleDuration())
							.isEqualTo(
									ScheduleDuration.createNotAllDay(
											LocalDateTime.of(2024, 1, 1, 0, 0),
											LocalDateTime.of(2024, 2, 1, 0, 0)));
					assertThat(schedules.get(0).getScheduleGroup()).isEqualTo(scheduleGroup);
				});
	}

	@Test
	@DisplayName("반복되는 schedule들을 추가할 수 있다.")
	void addRepeatingSchedules() {
		LocalDateTime repetitionStartDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime repetitionEndDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		RepetitionAddRequest repetitionAddRequest =
				new RepetitionAddRequest(
						RepetitionType.DAILY,
						1,
						repetitionStartDate,
						repetitionEndDate,
						null,
						null,
						null,
						null,
						null);
		ScheduleAddRequest scheduleAddRequest =
				new ScheduleAddRequest(
						"title",
						"description",
						false,
						startDate,
						endDate,
						true,
						repetitionAddRequest,
						"aaffbb",
						"서울역",
						true,
						36.0,
						127.0,
						true,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)));
		ScheduleGroup scheduleGroup = new ScheduleGroup(true);
		RepetitionPattern repetitionPattern =
				RepetitionPattern.createDaily(
						1, repetitionStartDate, repetitionEndDate, scheduleGroup);
		Schedule schedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDatetime(startDate)
						.endDatetime(endDate)
						.colorHex("aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.calendar(calendar)
						.scheduleGroup(scheduleGroup)
						.scheduleAlarms(List.of(LocalDateTime.of(2024, 1, 1, 0, 0)))
						.build();
		given(scheduleGroupService.addScheduleGroup(true)).willReturn(scheduleGroup);
		given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);
		given(repetitionPatternService.addRepetitionPattern(repetitionAddRequest, scheduleGroup))
				.willReturn(repetitionPattern);

		List<Schedule> schedules =
				scheduleCommandService.addSchedules(scheduleAddRequest, calendarId);

		assertAll(
				() -> {
					assertThat(schedules).hasSize(32);
					assertThat(schedules.get(0).getTitle()).isEqualTo("title");
					assertThat(schedules.get(0).getDescription()).isEqualTo("description");
					assertThat(schedules.get(0).getScheduleDuration())
							.isEqualTo(
									ScheduleDuration.createNotAllDay(
											LocalDateTime.of(2024, 1, 1, 0, 0),
											LocalDateTime.of(2024, 2, 1, 0, 0)));
				});
	}

	@Test
	@DisplayName("일정을 삭제할 수 있다.")
	void removeSchedule() {
		Member member = mock(Member.class);
		Calendar calendar = mock(Calendar.class);
		Schedule schedule = mock(Schedule.class);
		given(schedule.getCalendar()).willReturn(calendar);
		given(calendar.getMember()).willReturn(member);
		given(member.getId()).willReturn(1L);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));

		scheduleCommandService.removeSchedule(schedule.getId(), 1L);

		verify(schedule, times(1)).delete();
	}
}
