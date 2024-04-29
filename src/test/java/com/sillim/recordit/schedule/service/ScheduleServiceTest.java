package com.sillim.recordit.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.domain.vo.ScheduleDescription;
import com.sillim.recordit.schedule.domain.vo.ScheduleDuration;
import com.sillim.recordit.schedule.domain.vo.ScheduleTitle;
import com.sillim.recordit.schedule.dto.RepetitionAddRequest;
import com.sillim.recordit.schedule.dto.ScheduleAddRequest;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

	@Mock ScheduleRepository scheduleRepository;
	@Mock ScheduleGroupService scheduleGroupService;
	@Mock RepetitionPatternService repetitionPatternService;
	@InjectMocks ScheduleService scheduleService;

	Member member;
	Calendar calendar;
	long calendarId = 1L;
	long memberId = 1L;

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
		calendar = Calendar.builder().title("calendar1").colorHex("#aabbff").member(member).build();
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
						"#aaffbb",
						"서울역",
						true,
						36.0,
						127.0,
						true,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						calendarId);
		ScheduleGroup scheduleGroup =
				ScheduleGroup.builder().isRepeated(false).member(member).calendar(calendar).build();
		Schedule schedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDatetime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDatetime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.colorHex("#aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.alarmTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.scheduleGroup(scheduleGroup)
						.build();
		given(scheduleGroupService.addScheduleGroup(false, calendarId, memberId))
				.willReturn(scheduleGroup);
		given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);

		List<Schedule> schedules = scheduleService.addSchedules(scheduleAddRequest, memberId);

		assertAll(
				() -> {
					assertThat(schedules).hasSize(1);
					assertThat(schedules.get(0).getTitle()).isEqualTo(new ScheduleTitle("title"));
					assertThat(schedules.get(0).getDescription())
							.isEqualTo(new ScheduleDescription("description"));
					assertThat(schedules.get(0).getScheduleDuration())
							.isEqualTo(
									ScheduleDuration.createNotAllDay(
											LocalDateTime.of(2024, 1, 1, 0, 0),
											LocalDateTime.of(2024, 2, 1, 0, 0)));
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
						"#aaffbb",
						"서울역",
						true,
						36.0,
						127.0,
						true,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						calendarId);
		ScheduleGroup scheduleGroup =
				ScheduleGroup.builder().isRepeated(false).member(member).calendar(calendar).build();
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
						.colorHex("#aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.alarmTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.scheduleGroup(scheduleGroup)
						.build();
		given(scheduleGroupService.addScheduleGroup(true, calendarId, memberId))
				.willReturn(scheduleGroup);
		given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);
		given(repetitionPatternService.addRepetitionPattern(repetitionAddRequest, scheduleGroup))
				.willReturn(repetitionPattern);

		List<Schedule> schedules = scheduleService.addSchedules(scheduleAddRequest, memberId);

		assertAll(
				() -> {
					assertThat(schedules).hasSize(32);
					assertThat(schedules.get(0).getTitle()).isEqualTo(new ScheduleTitle("title"));
					assertThat(schedules.get(0).getDescription())
							.isEqualTo(new ScheduleDescription("description"));
					assertThat(schedules.get(0).getScheduleDuration())
							.isEqualTo(
									ScheduleDuration.createNotAllDay(
											LocalDateTime.of(2024, 1, 1, 0, 0),
											LocalDateTime.of(2024, 2, 1, 0, 0)));
				});
	}
}
