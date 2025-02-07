package com.sillim.recordit.schedule.service;

import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleAlarm;
import com.sillim.recordit.schedule.dto.response.DayScheduleResponse;
import com.sillim.recordit.schedule.dto.response.MonthScheduleResponse;
import com.sillim.recordit.schedule.dto.response.RepetitionPatternResponse;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleQueryService {

	private final ScheduleRepository scheduleRepository;
	private final RepetitionPatternService repetitionPatternService;
	private final CalendarQueryService calendarQueryService;

	public DayScheduleResponse searchSchedule(Long scheduleId, Long memberId) {
		Schedule schedule =
				scheduleRepository
						.findByScheduleId(scheduleId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
		schedule.validateAuthenticatedMember(memberId);

		if (schedule.getScheduleGroup().isRepeated()) {
			return DayScheduleResponse.of(
					schedule,
					true,
					schedule.getScheduleAlarms().stream().map(ScheduleAlarm::getAlarmTime).toList(),
					RepetitionPatternResponse.from(
							repetitionPatternService.searchByScheduleGroupId(
									schedule.getScheduleGroup().getId())));
		}

		return DayScheduleResponse.of(
				schedule,
				false,
				schedule.getScheduleAlarms().stream().map(ScheduleAlarm::getAlarmTime).toList(),
				null);
	}

	public List<MonthScheduleResponse> searchSchedulesInMonth(
			Long calendarId, Integer year, Integer month, Long memberId) {
		calendarQueryService.searchByCalendarId(calendarId).validateAuthenticatedMember(memberId);
		return scheduleRepository.findScheduleInMonth(calendarId, year, month).stream()
				.map(MonthScheduleResponse::from)
				.toList();
	}

	public List<DayScheduleResponse> searchSchedulesInDay(
			Long calendarId, LocalDate date, Long memberId) {
		calendarQueryService.searchByCalendarId(calendarId).validateAuthenticatedMember(memberId);
		return scheduleRepository.findScheduleInDay(calendarId, date).stream()
				.map(
						schedule ->
								DayScheduleResponse.of(
										schedule,
										schedule.getScheduleGroup().isRepeated(),
										schedule.getScheduleAlarms().stream()
												.map(ScheduleAlarm::getAlarmTime)
												.toList(),
										schedule.getScheduleGroup()
												.getRepetitionPattern()
												.map(RepetitionPatternResponse::from)
												.orElse(null)))
				.toList();
	}
}
