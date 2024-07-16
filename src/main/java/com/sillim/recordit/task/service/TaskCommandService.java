package com.sillim.recordit.task.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.dto.request.TaskAddRequest;
import com.sillim.recordit.task.dto.request.TaskUpdateRequest;
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

		final TaskGroup taskGroup =
				taskGroupService.addTaskGroup(
						request.isRepeated(),
						request.relatedMonthlyGoalId(),
						request.relatedWeeklyGoalId(),
						memberId);
		Calendar calendar = calendarService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);
		if (request.isRepeated()) {
			addRepeatingTasks(request, taskGroup, calendar);
			return;
		}
		taskRepository.save(request.toTask(calendar, taskGroup));
	}

	public void modifyAllTasksInGroupExcludeRepetition(
			final TaskUpdateRequest request,
			final Long calendarId,
			final Long selectedTaskId,
			final Long memberId) {

		Calendar calendar = calendarService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);

		Task selectedTask =
				taskRepository
						.findByIdAndCalendarId(selectedTaskId, calendarId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		selectedTask.validateAuthenticatedMember(memberId);

		modifyTasksInTaskGroup(request, calendarId, selectedTask.getTaskGroup().getId(), memberId);
	}

	private void addRepeatingTasks(
			final TaskAddRequest request, final TaskGroup taskGroup, final Calendar calendar) {
		repetitionPatternService
				.addRepetitionPattern(request.repetition(), taskGroup)
				.repeatingStream()
				.forEach(
						temporalAmount ->
								taskRepository.save(
										request.toTask(temporalAmount, calendar, taskGroup)));
	}

	private void modifyTasksInTaskGroup(
			final TaskUpdateRequest request,
			final Long calendarId,
			final Long taskGroupId,
			final Long memberId) {
		Calendar newCalendar = calendarService.searchByCalendarId(request.calendarId());
		newCalendar.validateAuthenticatedMember(memberId);
		taskRepository.updateAllByCalendarIdAndTaskGroupId(
				calendarId,
				taskGroupId,
				request.title(),
				request.description(),
				request.date(),
				request.colorHex(),
				newCalendar);
	}
}
