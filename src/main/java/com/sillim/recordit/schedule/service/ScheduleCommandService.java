package com.sillim.recordit.schedule.service;

import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.request.ScheduleAddRequest;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleCommandService {

	private final ScheduleRepository scheduleRepository;
	private final CalendarService calendarService;
	private final ScheduleGroupService scheduleGroupService;
	private final RepetitionPatternService repetitionPatternService;

	public List<Schedule> addSchedules(ScheduleAddRequest request, Long calendarId) {
		ScheduleGroup scheduleGroup = scheduleGroupService.addScheduleGroup(request.isRepeated());

		if (request.isRepeated()) {
			return addRepeatingSchedule(request, scheduleGroup, calendarId);
		}

		Schedule schedule =
				request.toSchedule(calendarService.searchByCalendarId(calendarId), scheduleGroup);
		return List.of(scheduleRepository.save(schedule));
	}

	public void removeSchedule(Long scheduleId, Long memberId) {
		Schedule schedule =
				scheduleRepository
						.findByScheduleId(scheduleId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
		validateAuthenticatedUser(memberId, schedule.getCalendar().getMember().getId());

		schedule.delete();
	}

	private List<Schedule> addRepeatingSchedule(
			ScheduleAddRequest request, ScheduleGroup scheduleGroup, Long calendarId) {
		return repetitionPatternService
				.addRepetitionPattern(request.repetition(), scheduleGroup)
				.repeatingStream()
				.map(
						temporalAmount ->
								scheduleRepository.save(
										request.toSchedule(
												temporalAmount,
												calendarService.searchByCalendarId(calendarId),
												scheduleGroup)))
				.toList();
	}

	private void validateAuthenticatedUser(Long principalId, Long memberId) {
		if (!principalId.equals(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}
	}
}
