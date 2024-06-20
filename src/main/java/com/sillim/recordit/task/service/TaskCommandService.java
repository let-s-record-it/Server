package com.sillim.recordit.task.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.dto.request.TaskAddRequest;
import com.sillim.recordit.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskCommandService {

	private final TaskGroupService taskGroupService;
	private final TaskRepetitionPatternService repetitionPatternService;
	private final CalendarService calendarService;

	private final TaskRepository taskRepository;

	public void addTasks(final TaskAddRequest request, final Long calendarId, final Long memberId) {

		calendarService.validateOwnerOfCalendar(calendarId, memberId);

		final TaskGroup taskGroup =
				taskGroupService.addTaskGroup(
						request.isRepeated(),
						request.relatedMonthlyGoalId(),
						request.relatedWeeklyGoalId(),
						memberId);
		final Calendar calendar = calendarService.searchByCalendarId(calendarId);
		if (request.isRepeated()) {
			addRepeatingTask(request, taskGroup, calendar);
			return;
		}
		taskRepository.save(request.toTask(calendar, taskGroup));
	}

	private void addRepeatingTask(
			final TaskAddRequest request, final TaskGroup taskGroup, final Calendar calendar) {
		repetitionPatternService
				.addRepetitionPattern(request.repetition(), taskGroup)
				.repeatingStream()
				.forEach(
						temporalAmount ->
								taskRepository.save(
										request.toTask(temporalAmount, calendar, taskGroup)));
	}
}
