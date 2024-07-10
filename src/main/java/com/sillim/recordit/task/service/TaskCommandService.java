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
import java.util.List;
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
			addNewRepeatingTask(request, taskGroup, calendar);
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

		removeTasksInGroupExceptCurrentTask(request.removeStrategy(), taskGroupId, taskId);

		TaskGroup newTaskGroup =
				taskGroupService.modifyTaskGroup(
						taskGroupId,
						request.isRepeated(),
						request.relatedMonthlyGoalId(),
						request.relatedWeeklyGoalId(),
						memberId);
		Calendar newCalendar = calendarService.searchByCalendarId(request.calendarId(), memberId);
		List<Task> modifiedTasks =
				modifyAllTasksInTaskGroup(
						request, calendarId, taskGroupId, newCalendar, newTaskGroup);

		if (request.isRepeated()) {
			addNewRepeatingTask(modifiedTasks, request, newTaskGroup, newCalendar);
		}
	}

	private void addNewRepeatingTask(
			final TaskAddRequest request, final TaskGroup taskGroup, final Calendar calendar) {
		repetitionPatternService
				.addRepetitionPattern(request.repetition(), taskGroup)
				.repeatingStream()
				.forEach(
						temporalAmount ->
								taskRepository.save(
										request.toTask(temporalAmount, calendar, taskGroup)));
	}

	/**
	 * 변경된 반복 패턴에 의해 생성된 새로운 task를 taskGroup에 추가한다.
	 * 해당 날짜에 동일한 task가 존재한다면 생성해주지 않는다.
	 * @param tasksInGroup 현재 taskGroup에 남아있는 task들
	 */
	private void addNewRepeatingTask(
			final List<Task> tasksInGroup,
			final TaskUpdateRequest request,
			final TaskGroup taskGroup,
			final Calendar calendar) {
		repetitionPatternService
				.modifyRepetitionPattern(request.repetition(), taskGroup)
				.repeatingStream()
				.forEach(
						temporalAmount -> {
							Task newTask = request.toTask(temporalAmount, calendar, taskGroup);
							if (tasksInGroup.stream()
									.noneMatch(task -> task.hasSameDate(newTask.getDate()))) {
								taskRepository.save(newTask);
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

	private List<Task> modifyAllTasksInTaskGroup(
			final TaskUpdateRequest request,
			final Long calendarId,
			final Long taskGroupId,
			final Calendar newCalendar,
			final TaskGroup newTaskGroup) {
		List<Task> tasksInGroup =
				taskRepository.findAllByCalendarIdAndTaskGroupId(calendarId, taskGroupId);
		tasksInGroup.forEach(
				task ->
						task.modify(
								request.title(),
								request.description(),
								request.date(),
								request.colorHex(),
								newCalendar,
								newTaskGroup));
		return tasksInGroup;
	}

	private void validateOwnerOfCalendar(Long calendarId, Long memberId) {
		calendarService.searchByCalendarId(calendarId, memberId);
	}
}
