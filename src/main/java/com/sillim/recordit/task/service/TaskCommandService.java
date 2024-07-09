package com.sillim.recordit.task.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRemoveStrategy;
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
		final Calendar calendar = calendarService.searchByCalendarId(calendarId, memberId);
		if (request.isRepeated()) {
			addRepeatingTask(request, taskGroup, calendar);
			return;
		}
		taskRepository.save(request.toTask(calendar, taskGroup));
	}

	public void modifyAllTasks(
			final TaskUpdateRequest request,
			final Long calendarId,
			final Long taskId,
			final Long memberId) {

		validateOwnerOfCalendar(calendarId, memberId);
		Task selectedTask =
				taskRepository
						.findByIdAndCalendarId(taskId, calendarId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		Long taskGroupId = selectedTask.getTaskGroup().getId();

		removeTasksInGroupExceptCurrentTask(
				request.removeStrategy(), taskGroupId, selectedTask.getId());

		TaskGroup newTaskGroup =
				taskGroupService.modifyTaskGroup(
						taskGroupId,
						request.isRepeated(),
						request.relatedMonthlyGoalId(),
						request.relatedWeeklyGoalId(),
						memberId); // selectedTask group 수정
		Calendar newCalendar = calendarService.searchByCalendarId(request.calendarId());
		modifyAllTasksInTaskGroup(request, calendarId, taskGroupId, newCalendar, newTaskGroup);

		if (request.isRepeated()) {
			addRepeatingTask(request, newTaskGroup, newCalendar);
		}
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

	private void addRepeatingTask(
			final TaskUpdateRequest request, final TaskGroup taskGroup, final Calendar calendar) {
		repetitionPatternService
				.modifyRepetitionPattern(request.repetition(), taskGroup)
				.repeatingStream()
				.forEach(
						temporalAmount -> {
							Task task = request.toTask(temporalAmount, calendar, taskGroup);
							if (!taskRepository.existsByTaskGroupIdAndDate(
									taskGroup.getId(), task.getDate())) {
								taskRepository.save(task);
							}
						});
	}

	/**
	 * 기존 task들 처리 방법 -> 1) 모두 삭제, 2) 달성하지 않은 것들만 삭제
	 * 현재 선택한 task 외의 tasks들에게 적용
	 */
	private void removeTasksInGroupExceptCurrentTask(
			final TaskRemoveStrategy strategy, final Long taskGroupId, final Long taskId) {
		switch (strategy) {
			case REMOVE_ALL ->
					taskRepository.deleteAllByTaskGroupIdAndTaskIdNot(taskGroupId, taskId);
			case REMOVE_NOT_ACHIEVED ->
					taskRepository.deleteAllNotAchievedTasksByTaskGroupIdAndTaskIdNot(
							taskGroupId, taskId);
		}
	}

	private void modifyAllTasksInTaskGroup(
			final TaskUpdateRequest request,
			final Long calendarId,
			final Long taskGroupId,
			final Calendar newCalendar,
			final TaskGroup newTaskGroup) {
		taskRepository
				.findAllByCalendarIdAndTaskGroupId(calendarId, taskGroupId)
				.forEach(
						task ->
								task.modify(
										request.title(),
										request.description(),
										request.date(),
										request.colorHex(),
										newCalendar,
										newTaskGroup));
	}

	private void validateOwnerOfCalendar(Long calendarId, Long memberId) {
		calendarService.searchByCalendarId(calendarId, memberId);
	}
}
