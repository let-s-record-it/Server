package com.sillim.recordit.schedule.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.dto.response.CalendarMemberResponse;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.service.ScheduleCategoryQueryService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.pushalarm.dto.AlarmType;
import com.sillim.recordit.pushalarm.dto.PushMessage;
import com.sillim.recordit.pushalarm.dto.ScheduleDeleteMessage;
import com.sillim.recordit.pushalarm.service.AlarmService;
import com.sillim.recordit.pushalarm.service.PushAlarmReserver;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleAlarm;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.request.RepetitionUpdateRequest;
import com.sillim.recordit.schedule.dto.request.ScheduleAddRequest;
import com.sillim.recordit.schedule.dto.request.ScheduleModifyRequest;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleCommandService {

	private static final long TO_SKIP_ADD_TEMPORAL = 0L;
	private static final long TO_SKIP_MODIFY_TEMPORAL = 1L;
	private static final String SCHEDULE_GROUP_PREFIX = "SCHEDULE/";
	public static final DateTimeFormatter ALARM_FORMATTER =
			DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)");

	private final ScheduleRepository scheduleRepository;
	private final CalendarQueryService calendarQueryService;
	private final ScheduleGroupService scheduleGroupService;
	private final RepetitionPatternService repetitionPatternService;
	private final PushAlarmReserver pushAlarmReserver;
	private final ScheduleCategoryQueryService scheduleCategoryQueryService;
	private final AlarmService alarmService;
	private final CalendarMemberService calendarMemberService;

	public List<Schedule> addSchedules(ScheduleAddRequest request, Long calendarId, Long memberId)
			throws SchedulerException {
		calendarMemberService.validateCalendarMember(calendarId, memberId);
		ScheduleCategory scheduleCategory =
				scheduleCategoryQueryService.searchScheduleCategory(request.categoryId());
		ScheduleGroup scheduleGroup = scheduleGroupService.newScheduleGroup(request.isRepeated());
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		List<Schedule> schedules;

		if (request.isRepeated()) {
			schedules =
					addRepeatingSchedule(
							temporalAmount ->
									scheduleRepository.save(
											request.toSchedule(
													temporalAmount,
													scheduleCategory,
													calendar,
													scheduleGroup)),
							request.repetition(),
							scheduleGroup,
							TO_SKIP_ADD_TEMPORAL);
		} else {
			schedules =
					List.of(
							scheduleRepository.save(
									request.toSchedule(scheduleCategory, calendar, scheduleGroup)));
		}

		Schedule standSchedule = schedules.get(0);
		pushAlarmReserver.reservePushAlarmJobs(
				standSchedule.getCalendar().getMemberId(),
				SCHEDULE_GROUP_PREFIX
						+ standSchedule.getCalendar().getMemberId()
						+ "/"
						+ scheduleGroup.getId(),
				standSchedule.getTitle(),
				standSchedule
						.getStartDateTime()
						.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)")),
				Map.of("scheduleId", standSchedule.getId()),
				standSchedule.getScheduleAlarms().stream()
						.map(ScheduleAlarm::getAlarmTime)
						.toList());

		calendarMemberService
				.searchCalendarMembers(calendarId)
				.forEach(
						member ->
								alarmService.pushAlarm(
										memberId,
										member.id(),
										PushMessage.fromAddSchedules(schedules)));
		return schedules;
	}

	public void modifySchedule(ScheduleModifyRequest request, Long scheduleId, Long memberId)
			throws SchedulerException {
		Schedule schedule =
				scheduleRepository
						.findByScheduleId(scheduleId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
		schedule.validateAuthenticatedMember(memberId);
		Calendar calendar = calendarQueryService.searchByCalendarId(request.calendarId());
		calendar.validateAuthenticatedMember(memberId);
		ScheduleCategory category =
				scheduleCategoryQueryService.searchScheduleCategory(request.categoryId());
		ScheduleGroup newScheduleGroup =
				scheduleGroupService.newScheduleGroup(request.isRepeated());

		pushAlarmReserver.deletePushAlarmJobs(
				SCHEDULE_GROUP_PREFIX + memberId + "/" + schedule.getScheduleGroup().getId());
		schedule.modify(
				request.title(),
				request.description(),
				request.isAllDay(),
				request.startDateTime(),
				request.endDateTime(),
				request.place(),
				request.setLocation(),
				request.latitude(),
				request.longitude(),
				request.setAlarm(),
				request.alarmTimes(),
				category,
				calendar,
				newScheduleGroup);
		pushAlarmReserver.reservePushAlarmJobs(
				memberId,
				SCHEDULE_GROUP_PREFIX + memberId + "/" + newScheduleGroup.getId(),
				schedule.getTitle(),
				schedule.getStartDateTime().format(ALARM_FORMATTER),
				Map.of("scheduleId", schedule.getId()),
				schedule.getScheduleAlarms().stream().map(ScheduleAlarm::getAlarmTime).toList());

		if (request.isRepeated()) {
			addRepeatingSchedule(
					temporalAmount ->
							scheduleRepository.save(
									request.toSchedule(
											temporalAmount, category, calendar, newScheduleGroup)),
					request.repetition(),
					newScheduleGroup,
					TO_SKIP_MODIFY_TEMPORAL);
		}
	}

	public void modifyGroupSchedules(ScheduleModifyRequest request, Long scheduleId, Long memberId)
			throws SchedulerException {
		Schedule schedule =
				scheduleRepository
						.findByScheduleId(scheduleId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
		schedule.validateAuthenticatedMember(memberId);
		Calendar calendar = calendarQueryService.searchByCalendarId(request.calendarId());
		calendar.validateAuthenticatedMember(memberId);
		ScheduleCategory category =
				scheduleCategoryQueryService.searchScheduleCategory(request.categoryId());
		ScheduleGroup scheduleGroup = schedule.getScheduleGroup();

		pushAlarmReserver.deletePushAlarmJobs(
				SCHEDULE_GROUP_PREFIX + memberId + "/" + scheduleGroup.getId());
		scheduleRepository.findGroupSchedules(scheduleGroup.getId()).forEach(Schedule::delete);

		if (request.isRepeated()) {
			repetitionPatternService
					.updateRepetitionPattern(request.repetition(), scheduleGroup)
					.repeatingStream()
					.forEach(
							temporalAmount ->
									scheduleRepository.save(
											request.toSchedule(
													temporalAmount,
													category,
													calendar,
													scheduleGroup)));
		} else {
			scheduleGroup.modifyNotRepeated();
			scheduleRepository.save(
					request.toSchedule(Period.ZERO, category, calendar, scheduleGroup));
		}

		pushAlarmReserver.reservePushAlarmJobs(
				memberId,
				SCHEDULE_GROUP_PREFIX + memberId + "/" + scheduleGroup.getId(),
				request.title(),
				request.startDateTime().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)")),
				Map.of("scheduleId", schedule.getId()),
				request.alarmTimes());
	}

	private List<Schedule> addRepeatingSchedule(
			Function<TemporalAmount, Schedule> temporalToSchedule,
			RepetitionUpdateRequest repetition,
			ScheduleGroup scheduleGroup,
			long toSkipTemporal) {
		return repetitionPatternService
				.addRepetitionPattern(repetition, scheduleGroup)
				.repeatingStream()
				.skip(toSkipTemporal)
				.map(temporalToSchedule)
				.toList();
	}

	public void removeSchedule(Long scheduleId, Long memberId) throws SchedulerException {
		Schedule schedule =
				scheduleRepository
						.findByScheduleId(scheduleId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
		Long calendarId = schedule.getCalendar().getId();
		calendarMemberService.validateCalendarMember(calendarId, memberId);

		pushAlarmReserver.deletePushAlarmJobs(
				SCHEDULE_GROUP_PREFIX + memberId + "/" + schedule.getScheduleGroup().getId());
		schedule.delete();

		List<CalendarMemberResponse> calendarMembers =
				calendarMemberService.searchCalendarMembers(calendarId);
		calendarMembers.forEach(
				calendarMember ->
						alarmService.pushAlarm(
								memberId,
								calendarMember.memberId(),
								PushMessage.fromDeleteSchedules(List.of(schedule))));
	}

	public void removeGroupSchedules(Long scheduleId, Long memberId) throws SchedulerException {
		Schedule schedule =
				scheduleRepository
						.findByScheduleId(scheduleId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
		Long calendarId = schedule.getCalendar().getId();
		calendarMemberService.validateCalendarMember(calendarId, memberId);

		pushAlarmReserver.deletePushAlarmJobs(
				SCHEDULE_GROUP_PREFIX + memberId + "/" + schedule.getScheduleGroup().getId());
		List<ScheduleDeleteMessage> messages =
				scheduleRepository.findGroupSchedules(schedule.getScheduleGroup().getId()).stream()
						.map(
								s -> {
									s.delete();
									return ScheduleDeleteMessage.from(s);
								})
						.toList();

		List<CalendarMemberResponse> calendarMembers =
				calendarMemberService.searchCalendarMembers(calendarId);
		calendarMembers.forEach(
				calendarMember ->
						alarmService.pushAlarm(
								memberId,
								calendarMember.memberId(),
								new PushMessage<>(null, AlarmType.SCHEDULE_DELETE, messages)));
	}

	public void removeGroupSchedulesAfterCurrent(Long scheduleId, Long memberId) {
		Schedule schedule =
				scheduleRepository
						.findByScheduleId(scheduleId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
		Long calendarId = schedule.getCalendar().getId();
		calendarMemberService.validateCalendarMember(calendarId, memberId);

		List<ScheduleDeleteMessage> messages =
				scheduleRepository
						.findGroupSchedulesAfterCurrent(
								schedule.getScheduleGroup().getId(), schedule.getStartDateTime())
						.stream()
						.map(
								s -> {
									s.delete();
									return ScheduleDeleteMessage.from(s);
								})
						.toList();

		List<CalendarMemberResponse> calendarMembers =
				calendarMemberService.searchCalendarMembers(calendarId);
		calendarMembers.forEach(
				calendarMember ->
						alarmService.pushAlarm(
								memberId,
								calendarMember.memberId(),
								new PushMessage<>(null, AlarmType.SCHEDULE_DELETE, messages)));
	}

	public void replaceScheduleCategoriesWithDefaultCategory(
			Long categoryId, Long calendarId, Long memberId) {
		ScheduleCategory defaultCategory =
				scheduleCategoryQueryService.searchDefaultCategory(calendarId, memberId);
		scheduleRepository.updateCategorySetDefault(defaultCategory.getId(), categoryId);
	}

	public void removeSchedulesInCalendar(Long calendarId) {
		scheduleRepository.deleteSchedulesInCalendar(calendarId);
	}
}
