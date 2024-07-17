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

	/**
	 * 반복 패턴을 제외한 외의 필드들을 수정하는 경우, 선택한 할 일이 속한 그룹 내의 할 일 모두를 수정한다.
	 */
	public void modifyAllTasksInGroup(
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

		taskGroupService.modifyTaskGroup(
				selectedTask.getTaskGroup(), request.taskGroup(), memberId);

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
