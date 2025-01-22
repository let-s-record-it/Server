package com.sillim.recordit.schedule.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.pushalarm.service.PushAlarmService;
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

	private final ScheduleRepository scheduleRepository;
	private final CalendarService calendarService;
	private final ScheduleGroupService scheduleGroupService;
	private final RepetitionPatternService repetitionPatternService;
	private final PushAlarmService pushAlarmService;

	public List<Schedule> addSchedules(ScheduleAddRequest request, Long calendarId)
			throws SchedulerException {
		ScheduleGroup scheduleGroup = scheduleGroupService.newScheduleGroup(request.isRepeated());
		List<Schedule> schedules;

		if (request.isRepeated()) {
			Calendar calendar = calendarService.searchByCalendarId(calendarId);
			schedules =
					addRepeatingSchedule(
							temporalAmount ->
									scheduleRepository.save(
											request.toSchedule(
													temporalAmount, calendar, scheduleGroup)),
							request.repetition(),
							scheduleGroup,
							TO_SKIP_ADD_TEMPORAL);

		} else {
			schedules =
					List.of(
							scheduleRepository.save(
									request.toSchedule(
											calendarService.searchByCalendarId(calendarId),
											scheduleGroup)));
		}

		Schedule standSchedule = schedules.get(0);
		pushAlarmService.reservePushAlarmJobs(
				standSchedule.getCalendar().getMember().getId(),
				SCHEDULE_GROUP_PREFIX
						+ standSchedule.getCalendar().getMember().getId()
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
		Calendar calendar = calendarService.searchByCalendarId(request.calendarId());
		calendar.validateAuthenticatedMember(memberId);
		ScheduleGroup newScheduleGroup =
				scheduleGroupService.newScheduleGroup(request.isRepeated());

		pushAlarmService.deletePushAlarmJobs(
				SCHEDULE_GROUP_PREFIX + memberId + "/" + schedule.getScheduleGroup().getId());
		schedule.modify(
				request.title(),
				request.description(),
				request.isAllDay(),
				request.startDateTime(),
				request.endDateTime(),
				request.colorHex(),
				request.place(),
				request.setLocation(),
				request.latitude(),
				request.longitude(),
				request.setAlarm(),
				request.alarmTimes(),
				calendar,
				newScheduleGroup);
		pushAlarmService.reservePushAlarmJobs(
				memberId,
				SCHEDULE_GROUP_PREFIX + memberId + "/" + newScheduleGroup.getId(),
				schedule.getTitle(),
				schedule.getStartDateTime()
						.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)")),
				Map.of("scheduleId", schedule.getId()),
				schedule.getScheduleAlarms().stream().map(ScheduleAlarm::getAlarmTime).toList());

		if (request.isRepeated()) {
			addRepeatingSchedule(
					temporalAmount ->
							scheduleRepository.save(
									request.toSchedule(temporalAmount, calendar, newScheduleGroup)),
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
		Calendar calendar = calendarService.searchByCalendarId(request.calendarId());
		calendar.validateAuthenticatedMember(memberId);
		ScheduleGroup scheduleGroup = schedule.getScheduleGroup();

		pushAlarmService.deletePushAlarmJobs(
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
													temporalAmount, calendar, scheduleGroup)));
		} else {
			scheduleGroup.modifyNotRepeated();
			scheduleRepository.save(request.toSchedule(Period.ZERO, calendar, scheduleGroup));
		}

		pushAlarmService.reservePushAlarmJobs(
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
		schedule.validateAuthenticatedMember(memberId);
		pushAlarmService.deletePushAlarmJobs(
				SCHEDULE_GROUP_PREFIX + memberId + "/" + schedule.getScheduleGroup().getId());

		schedule.delete();
	}

	public void removeGroupSchedules(Long scheduleId, Long memberId) throws SchedulerException {
		Schedule schedule =
				scheduleRepository
						.findByScheduleId(scheduleId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
		schedule.validateAuthenticatedMember(memberId);
		pushAlarmService.deletePushAlarmJobs(
				SCHEDULE_GROUP_PREFIX + memberId + "/" + schedule.getScheduleGroup().getId());

		scheduleRepository
				.findGroupSchedules(schedule.getScheduleGroup().getId())
				.forEach(Schedule::delete);
	}

	public void removeGroupSchedulesAfterCurrent(Long scheduleId, Long memberId) {
		Schedule schedule =
				scheduleRepository
						.findByScheduleId(scheduleId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
		schedule.validateAuthenticatedMember(memberId);

		scheduleRepository
				.findGroupSchedulesAfterCurrent(
						schedule.getScheduleGroup().getId(), schedule.getStartDateTime())
				.forEach(Schedule::delete);
	}
}
