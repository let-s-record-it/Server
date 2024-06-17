package com.sillim.recordit.task.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.dto.request.RemoveStrategy;
import com.sillim.recordit.task.dto.request.TaskAddRequest;
import com.sillim.recordit.task.dto.request.TaskRepetitionUpdateRequest;
import com.sillim.recordit.task.dto.request.TaskUpdateRequest;
import com.sillim.recordit.task.repository.TaskRepository;
import java.time.LocalDate;
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

		calendarService.validateOwnerOfCalendar(calendarId, memberId);

		final TaskGroup taskGroup =
				taskGroupService.add(
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

	public void modifyAllTasksInGroup(
			final TaskUpdateRequest request,
			final Long calendarId,
			final Long taskId,
			final Long memberId) {

		calendarService.validateOwnerOfCalendar(calendarId, memberId);

		final Task selectedTask =
				taskRepository
						.findByIdAndCalendarId(taskId, calendarId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		final TaskGroup taskGroup = selectedTask.getTaskGroup();
		final Calendar calendar = calendarService.searchByCalendarId(request.calendarId());

		switch (request.repetitionUpdateStatus()) {
				// 반복 패턴 유지 -> Task만 수정
			case NONE -> {
				// 1. TaskGroup 수정
				modifyTaskGroup(request, taskGroup, taskGroup.getIsRepeated(), memberId);
				// 2. 해당 TaskGroup에 속한 Task들 찾기
				final List<Task> tasksInGroup =
						taskRepository.findAllByTaskGroupId(taskGroup.getId());
				// 3. Task들 수정
				tasksInGroup.forEach(task -> modifyTask(request, task, calendar, taskGroup));
			}
				// 반복 패턴 삭제
			case DELETED -> {
				// 1. 기존에 존재하던 Tasks 처리
				removeExistingTasks(request.removeStrategy(), taskGroup, null);
				// 2. 반복 패턴 삭제
				repetitionPatternService.removeByTaskGroup(taskGroup);
				// 3. TaskGroup 수정
				modifyTaskGroup(request, taskGroup, false, memberId);
				// 4. 해당 TaskGroup에 속한 Task들 찾기
				final List<Task> tasksInGroup =
						taskRepository.findAllByTaskGroupId(taskGroup.getId());
				// 5. Task 수정
				tasksInGroup.forEach(task -> modifyTask(request, task, calendar, taskGroup));
			}
				// 반복 패턴 수정
			case MODIFIED -> {
				// 1. 기존에 존재하던 Tasks 처리
				removeExistingTasks(request.removeStrategy(), taskGroup, null);
				// 2. 반복 패턴 수정
				modifyRepetitionPattern(request.repetition(), taskGroup);
				// 3. TaskGroup 수정
				modifyTaskGroup(request, taskGroup, true, memberId);
				// 4. 해당 TaskGroup에 속한 Task들 찾기
				final List<Task> tasksInGroup =
						taskRepository.findAllByTaskGroupId(taskGroup.getId());
				// 5. Task 수정
				tasksInGroup.forEach(task -> modifyTask(request, task, calendar, taskGroup));
				// 6. 새로운 반복 패턴에 맞게 Task 생성 (해당 날짜에 이미 생성되어 있으면 넘어감)
				addRepeatingTaskIfNotExist(request, taskGroup, calendar);
			}
		}
	}

	private void modifyRepetitionPattern(
			final TaskRepetitionUpdateRequest request, final TaskGroup taskGroup) {
		// 2. 기존 TaskRepetitionPattern 삭제
		repetitionPatternService.removeByTaskGroup(taskGroup);
		// 3. 새로운 TaskRepetitionPattern 생성
		repetitionPatternService.add(request, taskGroup);
	}

	/**
	 * 반복 패턴에 변경이 있을 경우, 기존의 Task들을 처리
	 * @param strategy	Task 처리 전략(삭제 X, 달성하지 않은 Task만 삭제, 모두 삭제)
	 * @param date date + date 이후의 Task에 대해 처리 전략 적용
	 */
	private void removeExistingTasks(
			final RemoveStrategy strategy, final TaskGroup taskGroup, final LocalDate date) {

		switch (strategy) {
				// 달성하지 않은 Task만 삭제
			case REMOVE_NOT_ACHIEVED ->
					taskRepository.deleteAllByTaskGroupIdAndAchievedIsFalse(
							taskGroup.getId(), date);
				// 모두 삭제
			case REMOVE_ALL -> taskRepository.deleteAllByTaskGroupId(taskGroup.getId(), date);
		}
	}

	private void modifyTaskGroup(
			final TaskUpdateRequest request,
			final TaskGroup taskGroup,
			final Boolean isRepeated,
			final Long memberId) {
		taskGroupService.modify(
				taskGroup,
				isRepeated,
				request.relatedMonthlyGoalId(),
				request.relatedWeeklyGoalId(),
				memberId);
	}

	private void modifyTask(
			final TaskUpdateRequest request,
			final Task task,
			final Calendar calendar,
			final TaskGroup taskGroup) {
		task.modify(
				request.title(),
				request.description(),
				request.date(),
				request.colorHex(),
				calendar,
				taskGroup);
	}

	private void addRepeatingTask(
			final TaskAddRequest request, final TaskGroup taskGroup, final Calendar calendar) {
		repetitionPatternService
				.add(request.repetition(), taskGroup)
				.repeatingStream()
				.forEach(
						temporalAmount ->
								taskRepository.save(
										request.toTask(temporalAmount, calendar, taskGroup)));
	}

	private void addRepeatingTaskIfNotExist(
			final TaskUpdateRequest request, final TaskGroup taskGroup, final Calendar calendar) {
		repetitionPatternService
				.add(request.repetition(), taskGroup)
				.repeatingStream()
				.forEach(
						temporalAmount -> {
							if (!taskRepository.existsByTaskGroupIdAndDateEquals(
									taskGroup.getId(), request.date())) {
								taskRepository.save(
										request.toTask(temporalAmount, calendar, taskGroup));
							}
						});
	}
}
