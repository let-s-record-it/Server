package com.sillim.recordit.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.category.service.ScheduleCategoryQueryService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.pushalarm.service.PushAlarmReserver;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
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
import org.quartz.SchedulerException;

@ExtendWith(MockitoExtension.class)
class ScheduleCommandServiceTest {

	@Mock ScheduleRepository scheduleRepository;
	@Mock CalendarQueryService calendarQueryService;
	@Mock ScheduleGroupService scheduleGroupService;
	@Mock RepetitionPatternService repetitionPatternService;
	@Mock PushAlarmReserver pushAlarmReserver;
	@Mock ScheduleCategoryQueryService scheduleCategoryQueryService;
	@InjectMocks ScheduleCommandService scheduleCommandService;

	@Test
	@DisplayName("반복 없는 schedule을 추가할 수 있다.")
	void addSchedule() throws SchedulerException {
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		ScheduleAddRequest scheduleAddRequest =
				new ScheduleAddRequest(
						"title",
						"description",
						false,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0),
						false,
						null,
						"서울역",
						true,
						36.0,
						127.0,
						true,
						1L,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)));
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		ScheduleCategory scheduleCategory =
				ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		Schedule schedule =
				spy(ScheduleFixture.DEFAULT.getSchedule(scheduleCategory, scheduleGroup, calendar));
		given(schedule.getId()).willReturn(1L);
		given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);
		given(scheduleGroupService.newScheduleGroup(any())).willReturn(scheduleGroup);

		List<Schedule> schedules = scheduleCommandService.addSchedules(scheduleAddRequest, 1L);

		assertAll(
				() -> {
					assertThat(schedules).hasSize(1);
					assertThat(schedules.get(0).getTitle()).isEqualTo("title");
					assertThat(schedules.get(0).getDescription()).isEqualTo("description");
					assertThat(schedules.get(0).getScheduleDuration().getStartDateTime())
							.isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
					assertThat(schedules.get(0).getScheduleDuration().getEndDateTime())
							.isEqualTo(LocalDateTime.of(2024, 1, 2, 0, 0));
					assertThat(schedules.get(0).getScheduleGroup()).isEqualTo(scheduleGroup);
				});
	}

	@Test
	@DisplayName("반복되는 schedule들을 추가할 수 있다.")
	void addRepeatingSchedules() throws FirebaseMessagingException, SchedulerException {
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
						"서울역",
						true,
						36.0,
						127.0,
						true,
						1L,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)));
		ScheduleGroup scheduleGroup = new ScheduleGroup(true);
		RepetitionPattern repetitionPattern =
				RepetitionPattern.createDaily(
						1, repetitionStartDate, repetitionEndDate, scheduleGroup);
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		ScheduleCategory scheduleCategory =
				ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		Schedule schedule =
				spy(ScheduleFixture.DEFAULT.getSchedule(scheduleCategory, scheduleGroup, calendar));
		given(schedule.getId()).willReturn(1L);
		given(scheduleGroupService.newScheduleGroup(true)).willReturn(scheduleGroup);
		given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);
		given(repetitionPatternService.addRepetitionPattern(repetitionUpdateRequest, scheduleGroup))
				.willReturn(repetitionPattern);

		List<Schedule> schedules = scheduleCommandService.addSchedules(scheduleAddRequest, 1L);

		assertAll(
				() -> {
					assertThat(schedules).hasSize(32);
					assertThat(schedules.get(0).getTitle()).isEqualTo("title");
					assertThat(schedules.get(0).getDescription()).isEqualTo("description");
					assertThat(schedules.get(0).getScheduleDuration().getStartDateTime())
							.isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
					assertThat(schedules.get(0).getScheduleDuration().getEndDateTime())
							.isEqualTo(LocalDateTime.of(2024, 1, 2, 0, 0));
					assertThat(schedules.get(0).getScheduleDuration().isAllDay()).isEqualTo(false);
				});
	}

	@Test
	@DisplayName("단일 일정을 수정할 수 있다.")
	void modifySchedule() throws SchedulerException {
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
						"용산역",
						true,
						37.0,
						128.0,
						true,
						1L,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						1L);
		ScheduleGroup scheduleGroup = new ScheduleGroup(true);
		RepetitionPattern repetitionPattern =
				RepetitionPattern.createDaily(
						1, repetitionStartDate, repetitionEndDate, scheduleGroup);
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		ScheduleCategory scheduleCategory =
				spy(ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar));
		Schedule schedule =
				spy(ScheduleFixture.DEFAULT.getSchedule(scheduleCategory, scheduleGroup, calendar));
		given(schedule.getId()).willReturn(scheduleId);
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(1L)))
				.willReturn(scheduleCategory);
		given(scheduleRepository.findByScheduleId(scheduleId)).willReturn(Optional.of(schedule));
		given(calendarQueryService.searchByCalendarId(calendarId)).willReturn(calendar);
		given(scheduleGroupService.newScheduleGroup(true)).willReturn(scheduleGroup);
		given(repetitionPatternService.addRepetitionPattern(repetitionUpdateRequest, scheduleGroup))
				.willReturn(repetitionPattern);

		scheduleCommandService.modifySchedule(scheduleModifyRequest, scheduleId, memberId);

		assertAll(
				() -> {
					assertThat(schedule.getTitle()).isEqualTo("title2");
					assertThat(schedule.getDescription()).isEqualTo("description2");
					assertThat(schedule.isAllDay()).isTrue();
					assertThat(schedule.getColorHex()).isEqualTo("aaffbb");
					assertThat(schedule.getPlace()).isEqualTo("용산역");
					assertThat(schedule.getLatitude()).isEqualTo(37.0);
					assertThat(schedule.getLongitude()).isEqualTo(128.0);
					assertThat(schedule.isSetLocation()).isTrue();
					then(scheduleRepository).should(times(31)).save(any(Schedule.class));
				});
	}

	@Test
	@DisplayName("그룹 일정을 반복이 있는 그룹 일정으로 수정할 수 있다.")
	void modifyGroupSchedulesToRepeated() throws SchedulerException {
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
						"용산역",
						true,
						37.0,
						128.0,
						true,
						1L,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						1L);
		ScheduleGroup scheduleGroup = mock(ScheduleGroup.class);
		RepetitionPattern repetitionPattern =
				RepetitionPattern.createDaily(
						1, repetitionStartDate, repetitionEndDate, scheduleGroup);
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		ScheduleCategory scheduleCategory =
				ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		Schedule schedule =
				spy(ScheduleFixture.DEFAULT.getSchedule(scheduleCategory, scheduleGroup, calendar));
		given(schedule.getId()).willReturn(scheduleId);
		given(scheduleGroup.getId()).willReturn(1L);
		given(scheduleRepository.findByScheduleId(scheduleId)).willReturn(Optional.of(schedule));
		given(calendarQueryService.searchByCalendarId(calendarId)).willReturn(calendar);
		given(scheduleRepository.findGroupSchedules(eq(1L))).willReturn(List.of(schedule));
		given(
						repetitionPatternService.updateRepetitionPattern(
								repetitionUpdateRequest, scheduleGroup))
				.willReturn(repetitionPattern);

		scheduleCommandService.modifyGroupSchedules(scheduleModifyRequest, scheduleId, memberId);

		then(scheduleRepository).should(times(32)).save(any(Schedule.class));
	}

	@Test
	@DisplayName("그룹 일정을 반복이 없는 그룹 일정으로 수정할 수 있다.")
	void modifyGroupSchedulesToNotRepeated() throws SchedulerException {
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
						"용산역",
						true,
						37.0,
						128.0,
						true,
						1L,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						1L);
		ScheduleGroup scheduleGroup = mock(ScheduleGroup.class);
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		ScheduleCategory scheduleCategory =
				ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		Schedule schedule =
				spy(ScheduleFixture.DEFAULT.getSchedule(scheduleCategory, scheduleGroup, calendar));
		given(scheduleGroup.getId()).willReturn(1L);
		given(schedule.getId()).willReturn(1L);
		given(scheduleRepository.findByScheduleId(scheduleId)).willReturn(Optional.of(schedule));
		given(calendarQueryService.searchByCalendarId(calendarId)).willReturn(calendar);
		given(scheduleRepository.findGroupSchedules(eq(1L))).willReturn(List.of(schedule));

		scheduleCommandService.modifyGroupSchedules(scheduleModifyRequest, scheduleId, memberId);

		then(scheduleGroup).should(times(1)).modifyNotRepeated();
		then(scheduleRepository).should(times(1)).save(any(Schedule.class));
	}

	@Test
	@DisplayName("일정을 삭제할 수 있다.")
	void removeSchedule() throws SchedulerException {
		Schedule schedule = mock(Schedule.class);
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		given(schedule.getScheduleGroup()).willReturn(scheduleGroup);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));

		scheduleCommandService.removeSchedule(schedule.getId(), 1L);

		verify(schedule, times(1)).delete();
	}

	@Test
	@DisplayName("그룹 내 일정을 삭제할 수 있다.")
	void removeGroupSchedules() throws SchedulerException {
		Schedule schedule = mock(Schedule.class);
		ScheduleGroup scheduleGroup = mock(ScheduleGroup.class);
		given(schedule.getScheduleGroup()).willReturn(scheduleGroup);
		given(scheduleGroup.getId()).willReturn(1L);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));
		given(scheduleRepository.findGroupSchedules(eq(1L)))
				.willReturn(List.of(schedule, schedule, schedule));

		scheduleCommandService.removeGroupSchedules(schedule.getId(), 1L);

		then(schedule).should(times(3)).delete();
	}

	@Test
	@DisplayName("그룹 내 특정 일 이후 일정을 삭제할 수 있다.")
	void removeGroupSchedulesAfter() {
		Schedule schedule = mock(Schedule.class);
		ScheduleGroup scheduleGroup = mock(ScheduleGroup.class);
		given(schedule.getScheduleGroup()).willReturn(scheduleGroup);
		given(schedule.getStartDateTime()).willReturn(LocalDateTime.of(2024, 1, 1, 0, 0));
		given(scheduleGroup.getId()).willReturn(1L);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));
		given(scheduleRepository.findGroupSchedulesAfterCurrent(eq(1L), any(LocalDateTime.class)))
				.willReturn(List.of(schedule, schedule, schedule));

		scheduleCommandService.removeGroupSchedulesAfterCurrent(schedule.getId(), 1L);

		then(schedule).should(times(3)).delete();
	}

	@Test
	@DisplayName("그룹 내 일정 삭제 시 해당 일정의 유저가 아니면 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionIfNotOwnerWhenRemoveGroupSchedules() {
		long memberId = 1L;
		Member member = mock(Member.class);
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		ScheduleCategory scheduleCategory =
				ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		Schedule schedule =
				ScheduleFixture.DEFAULT.getSchedule(scheduleCategory, scheduleGroup, calendar);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));

		assertThatCode(() -> scheduleCommandService.removeGroupSchedules(schedule.getId(), 2L))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}

	@Test
	@DisplayName("그룹 내 특정 일 이후 일정 삭제 시 해당 일정의 유저가 아니면 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionIfNotOwnerWhenRemoveGroupSchedulesAfter() {
		long memberId = 1L;
		Member member = mock(Member.class);
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		ScheduleCategory scheduleCategory =
				ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		Schedule schedule =
				ScheduleFixture.DEFAULT.getSchedule(scheduleCategory, scheduleGroup, calendar);
		given(scheduleRepository.findByScheduleId(any())).willReturn(Optional.of(schedule));

		assertThatCode(
						() ->
								scheduleCommandService.removeGroupSchedulesAfterCurrent(
										schedule.getId(), 2L))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}
}
