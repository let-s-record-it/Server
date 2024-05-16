package com.sillim.recordit.schedule.service;

import com.sillim.recordit.calendar.service.CalendarService;
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
		return List.of(
				scheduleRepository.save(
						request.toSchedule(
								calendarService.findByCalendarId(calendarId),
								scheduleGroup)));
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
												calendarService.findByCalendarId(calendarId),
												scheduleGroup)))
				.toList();
	}
}
