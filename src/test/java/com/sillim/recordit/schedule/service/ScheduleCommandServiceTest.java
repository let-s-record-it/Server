package com.sillim.recordit.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.domain.vo.ScheduleDuration;
import com.sillim.recordit.schedule.dto.request.RepetitionUpdateRequest;
import com.sillim.recordit.schedule.dto.request.ScheduleAddRequest;
import com.sillim.recordit.schedule.dto.request.ScheduleModifyRequest;
import com.sillim.recordit.schedule.fixture.ScheduleFixture;
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
class ScheduleCommandServiceTest {

	@Mock ScheduleRepository scheduleRepository;
	@Mock CalendarService calendarService;
	@Mock ScheduleGroupService scheduleGroupService;
	@Mock RepetitionPatternService repetitionPatternService;
	@InjectMocks ScheduleCommandService scheduleCommandService;

	@Test
	@DisplayName("반복 없는 schedule을 추가할 수 있다.")
	void addSchedule() {
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(MemberFixture.DEFAULT.getMember());
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
						.startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDateTime(LocalDateTime.of(2024, 2, 1, 0, 0))
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

		List<Schedule> schedules = scheduleCommandService.addSchedules(scheduleAddRequest, 1L);

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
		RepetitionUpdateRequest repetitionUpdateRequest =
				new RepetitionUpdateRequest(
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
						repetitionUpdateRequest,
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
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(MemberFixture.DEFAULT.getMember());
		Schedule schedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDateTime(startDate)
						.endDateTime(endDate)
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
		given(repetitionPatternService.addRepetitionPattern(repetitionUpdateRequest, scheduleGroup))
				.willReturn(repetitionPattern);

		List<Schedule> schedules = scheduleCommandService.addSchedules(scheduleAddRequest, 1L);

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
	@DisplayName("단일 일정을 수정할 수 있다.")
	void throwInvalidRequestExceptionIfNotOwnerWhenModifySchedule() {
		LocalDateTime repetitionStartDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime repetitionEndDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		RepetitionUpdateRequest repetitionUpdateRequest =
				new RepetitionUpdateRequest(
						RepetitionType.DAILY,
						1,
						repetitionStartDate,
						repetitionEndDate,
						null,
						null,
						null,
						null,
						null);
		long memberId = 1L;
		long scheduleId = 1L;
		long calendarId = 1L;
		ScheduleModifyRequest scheduleModifyRequest =
				new ScheduleModifyRequest(
						"title2",
						"description2",
						true,
						startDate,
						endDate,
						true,
						repetitionUpdateRequest,
						"aaffcc",
						"용산역",
						true,
						37.0,
						128.0,
						true,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						1L);
		ScheduleGroup scheduleGroup = new ScheduleGroup(true);
		RepetitionPattern repetitionPattern =
				RepetitionPattern.createDaily(
						1, repetitionStartDate, repetitionEndDate, scheduleGroup);
		Member member1 = mock(Member.class);
		Member member2 = mock(Member.class);
		Calendar calendar1 = CalendarFixture.DEFAULT.getCalendar(member1);
		Calendar calendar2 = CalendarFixture.DEFAULT.getCalendar(member2);
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar1);
		given(member1.equalsId(anyLong())).willReturn(true);
		given(member2.equalsId(anyLong())).willReturn(false);
		given(scheduleRepository.findByScheduleId(scheduleId)).willReturn(Optional.of(schedule));
		given(calendarService.searchByCalendarId(calendarId)).willReturn(calendar2);

		assertThatCode(
						() ->
								scheduleCommandService.modifySchedule(
										scheduleModifyRequest, scheduleId, memberId))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}

	@Test
	@DisplayName("단일 일정을 수정할 수 있다.")
	void modifySchedule() {
		LocalDateTime repetitionStartDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime repetitionEndDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		RepetitionUpdateRequest repetitionUpdateRequest =
				new RepetitionUpdateRequest(
						RepetitionType.DAILY,
						1,
						repetitionStartDate,
						repetitionEndDate,
						null,
						null,
						null,
						null,
						null);
		long memberId = 1L;
		long scheduleId = 1L;
		long calendarId = 1L;
		ScheduleModifyRequest scheduleModifyRequest =
				new ScheduleModifyRequest(
						"title2",
						"description2",
						true,
						startDate,
						endDate,
						true,
						repetitionUpdateRequest,
						"aaffcc",
						"용산역",
						true,
						37.0,
						128.0,
						true,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						1L);
		ScheduleGroup scheduleGroup = new ScheduleGroup(true);
		RepetitionPattern repetitionPattern =
				RepetitionPattern.createDaily(
						1, repetitionStartDate, repetitionEndDate, scheduleGroup);
		Member member = mock(Member.class);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member);
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar);
		given(member.equalsId(anyLong())).willReturn(true);
		given(scheduleRepository.findByScheduleId(scheduleId)).willReturn(Optional.of(schedule));
		given(calendarService.searchByCalendarId(calendarId)).willReturn(calendar);
		given(scheduleGroupService.addScheduleGroup(true)).willReturn(scheduleGroup);
		given(repetitionPatternService.addRepetitionPattern(repetitionUpdateRequest, scheduleGroup))
				.willReturn(repetitionPattern);

		scheduleCommandService.modifySchedule(scheduleModifyRequest, scheduleId, memberId);

		assertAll(
				() -> {
					assertThat(schedule.getTitle()).isEqualTo("title2");
					assertThat(schedule.getDescription()).isEqualTo("description2");
					assertThat(schedule.isAllDay()).isTrue();
					assertThat(schedule.getColorHex()).isEqualTo("aaffcc");
					assertThat(schedule.getPlace()).isEqualTo("용산역");
					assertThat(schedule.getLatitude()).isEqualTo(37.0);
					assertThat(schedule.getLongitude()).isEqualTo(128.0);
					assertThat(schedule.isSetLocation()).isTrue();
					then(scheduleRepository).should(times(31)).save(any(Schedule.class));
				});
	}

	@Test
	@DisplayName("그룹 일정을 반복이 있는 그룹 일정으로 수정할 수 있다.")
	void modifySchedulesInGroupToRepeated() {
		LocalDateTime repetitionStartDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime repetitionEndDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		RepetitionUpdateRequest repetitionUpdateRequest =
				new RepetitionUpdateRequest(
						RepetitionType.DAILY,
						1,
						repetitionStartDate,
						repetitionEndDate,
						null,
						null,
						null,
						null,
						null);
		long memberId = 1L;
		long scheduleId = 1L;
		long calendarId = 1L;
		ScheduleModifyRequest scheduleModifyRequest =
				new ScheduleModifyRequest(
						"title2",
						"description2",
						true,
						startDate,
						endDate,
						true,
						repetitionUpdateRequest,
						"aaffcc",
						"용산역",
						true,
						37.0,
						128.0,
						true,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						1L);
		ScheduleGroup scheduleGroup = mock(ScheduleGroup.class);
		RepetitionPattern repetitionPattern =
				RepetitionPattern.createDaily(
						1, repetitionStartDate, repetitionEndDate, scheduleGroup);
		Member member = mock(Member.class);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member);
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar);
		given(member.equalsId(anyLong())).willReturn(true);
		given(scheduleGroup.getId()).willReturn(1L);
		given(scheduleRepository.findByScheduleId(scheduleId)).willReturn(Optional.of(schedule));
		given(calendarService.searchByCalendarId(calendarId)).willReturn(calendar);
		given(scheduleRepository.findSchedulesInGroup(eq(1L))).willReturn(List.of(schedule));
		given(
						repetitionPatternService.updateRepetitionPattern(
								repetitionUpdateRequest, scheduleGroup))
				.willReturn(repetitionPattern);

		scheduleCommandService.modifySchedulesInGroup(scheduleModifyRequest, scheduleId, memberId);

		then(scheduleRepository).should(times(32)).save(any(Schedule.class));
	}

	@Test
	@DisplayName("그룹 일정을 반복이 없는 그룹 일정으로 수정할 수 있다.")
	void modifySchedulesInGroupToNotRepeated() {
		LocalDateTime repetitionStartDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime repetitionEndDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		RepetitionUpdateRequest repetitionUpdateRequest =
				new RepetitionUpdateRequest(
						RepetitionType.DAILY,
						1,
						repetitionStartDate,
						repetitionEndDate,
						null,
						null,
						null,
						null,
						null);
		long memberId = 1L;
		long scheduleId = 1L;
		long calendarId = 1L;
		ScheduleModifyRequest scheduleModifyRequest =
				new ScheduleModifyRequest(
						"title2",
						"description2",
						true,
						startDate,
						endDate,
						false,
						repetitionUpdateRequest,
						"aaffcc",
						"용산역",
						true,
						37.0,
						128.0,
						true,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						1L);
		ScheduleGroup scheduleGroup = mock(ScheduleGroup.class);
		Member member = mock(Member.class);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member);
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar);
		given(member.equalsId(anyLong())).willReturn(true);
		given(scheduleGroup.getId()).willReturn(1L);
		given(scheduleRepository.findByScheduleId(scheduleId)).willReturn(Optional.of(schedule));
		given(calendarService.searchByCalendarId(calendarId)).willReturn(calendar);
		given(scheduleRepository.findSchedulesInGroup(eq(1L))).willReturn(List.of(schedule));

		scheduleCommandService.modifySchedulesInGroup(scheduleModifyRequest, scheduleId, memberId);

		then(scheduleGroup).should(times(1)).modifyNotRepeated();
		then(scheduleRepository).should(times(1)).save(any(Schedule.class));
	}

	@Test
	@DisplayName("일정을 삭제할 수 있다.")
	void removeSchedule() {
		Schedule schedule = mock(Schedule.class);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));

		scheduleCommandService.removeSchedule(schedule.getId(), 1L);

		verify(schedule, times(1)).delete();
	}

	@Test
	@DisplayName("그룹 내 일정을 삭제할 수 있다.")
	void removeSchedulesInGroup() {
		Schedule schedule = mock(Schedule.class);
		ScheduleGroup scheduleGroup = mock(ScheduleGroup.class);
		given(schedule.getScheduleGroup()).willReturn(scheduleGroup);
		given(scheduleGroup.getId()).willReturn(1L);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));
		given(scheduleRepository.findSchedulesInGroup(eq(1L)))
				.willReturn(List.of(schedule, schedule, schedule));

		scheduleCommandService.removeSchedulesInGroup(schedule.getId(), 1L);

		then(schedule).should(times(3)).delete();
	}

	@Test
	@DisplayName("그룹 내 특정 일 이후 일정을 삭제할 수 있다.")
	void removeSchedulesInGroupAfter() {
		Schedule schedule = mock(Schedule.class);
		ScheduleGroup scheduleGroup = mock(ScheduleGroup.class);
		given(schedule.getScheduleGroup()).willReturn(scheduleGroup);
		given(schedule.getStartDateTime()).willReturn(LocalDateTime.of(2024, 1, 1, 0, 0));
		given(scheduleGroup.getId()).willReturn(1L);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));
		given(scheduleRepository.findSchedulesInGroupAfter(eq(1L), any(LocalDateTime.class)))
				.willReturn(List.of(schedule, schedule, schedule));

		scheduleCommandService.removeSchedulesInGroupAfter(schedule.getId(), 1L);

		then(schedule).should(times(3)).delete();
	}

	@Test
	@DisplayName("그룹 내 일정 삭제 시 해당 일정의 유저가 아니면 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionIfNotOwnerWhenRemoveSchedulesInGroup() {
		Member member = mock(Member.class);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member);
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar);
		given(member.equalsId(anyLong())).willReturn(false);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));

		assertThatCode(() -> scheduleCommandService.removeSchedulesInGroup(schedule.getId(), 1L))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}

	@Test
	@DisplayName("그룹 내 특정 일 이후 일정 삭제 시 해당 일정의 유저가 아니면 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionIfNotOwnerWhenRemoveSchedulesInGroupAfter() {
		Member member = mock(Member.class);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member);
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar);
		given(member.equalsId(anyLong())).willReturn(false);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));

		assertThatCode(
						() ->
								scheduleCommandService.removeSchedulesInGroupAfter(
										schedule.getId(), 1L))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}
}
