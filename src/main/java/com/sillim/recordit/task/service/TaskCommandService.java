package com.sillim.recordit.task.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.service.ScheduleCategoryQueryService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.dto.request.TaskAddRequest;
import com.sillim.recordit.task.dto.request.TaskUpdateRequest;
import com.sillim.recordit.task.repository.TaskRepository;
import java.time.temporal.TemporalAmount;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TaskCommandService {

	private final TaskGroupService taskGroupService;
	private final CalendarQueryService calendarQueryService;
	private final ScheduleCategoryQueryService scheduleCategoryQueryService;

	private final TaskRepository taskRepository;

	public void addTasks(final TaskAddRequest request, final Long calendarId, final Long memberId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);
		ScheduleCategory category =
				scheduleCategoryQueryService.searchScheduleCategory(request.categoryId());
		if (request.isRepeated()) {
			TaskGroup taskGroup =
					taskGroupService.addRepeatingTaskGroup(
							request.taskGroup(), request.repetition(), memberId);
			addRepeatingTasks(
					temporalAmount -> request.toTask(temporalAmount, category, calendar, taskGroup),
					taskGroup);
			return;
		}
		TaskGroup taskGroup =
				taskGroupService.addNonRepeatingTaskGroup(request.taskGroup(), memberId);
		taskRepository.save(request.toTask(category, calendar, taskGroup));
	}

	public void resetTaskGroupAndAddNewTasks(
			final TaskUpdateRequest request,
			final Long calendarId,
			final Long selectedTaskId,
			final Long memberId) {

		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);

		Task selectedTask =
				taskRepository
						.findByIdAndCalendarId(selectedTaskId, calendarId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		TaskGroup taskGroup = selectedTask.getTaskGroup();
		taskRepository.deleteAllByTaskGroupId(taskGroup.getId()); // 기존에 있던 Task 모두 삭제

		Calendar newCalendar = calendarQueryService.searchByCalendarId(request.newCalendarId());
		newCalendar.validateAuthenticatedMember(memberId);
		ScheduleCategory newCategory =
				scheduleCategoryQueryService.searchScheduleCategory(request.newCategoryId());
		if (request.isRepeated()) {
			TaskGroup newTaskGroup =
					taskGroupService.modifyTaskGroupAndMakeRepeatable(
							taskGroup.getId(),
							request.newTaskGroup(),
							request.newRepetition(),
							memberId);
			addRepeatingTasks(
					temporalAmount ->
							request.toTask(temporalAmount, newCategory, newCalendar, newTaskGroup),
					newTaskGroup);
			return;
		}
		TaskGroup newTaskGroup =
				taskGroupService.modifyTaskGroupAndMakeNonRepeatable(
						taskGroup.getId(), request.newTaskGroup(), memberId);
		taskRepository.save(request.toTask(newCategory, newCalendar, newTaskGroup));
	}

	public void modifyOne(
			final TaskUpdateRequest request,
			final Long calendarId,
			final Long selectedTaskId,
			final Long memberId) {

		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);

		Task selectedTask =
				taskRepository
						.findByIdAndCalendarId(selectedTaskId, calendarId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		TaskGroup taskGroup = selectedTask.getTaskGroup();

		Calendar newCalendar = calendarQueryService.searchByCalendarId(request.newCalendarId());
		newCalendar.validateAuthenticatedMember(memberId);
		ScheduleCategory newCategory =
				scheduleCategoryQueryService.searchScheduleCategory(request.newCategoryId());
		if (request.isRepeated()) {
			selectedTask.remove();
			TaskGroup newTaskGroup =
					taskGroupService.modifyTaskGroupAndMakeRepeatable(
							taskGroup.getId(),
							request.newTaskGroup(),
							request.newRepetition(),
							memberId);
			addRepeatingTasks(
					temporalAmount ->
							request.toTask(temporalAmount, newCategory, newCalendar, newTaskGroup),
					newTaskGroup);
			return;
		}
		TaskGroup newTaskGroup =
				taskGroupService.modifyTaskGroup(
						taskGroup.getId(), request.newTaskGroup(), memberId);
		selectedTask.modify(
				request.newTitle(),
				request.newDescription(),
				request.date(),
				newCategory,
				newCalendar,
				newTaskGroup);
	}

	public void removeAll(final Long calendarId, final Long selectedTaskId, final Long memberId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);

		Task selectedTask =
				taskRepository
						.findByIdAndCalendarId(selectedTaskId, calendarId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		TaskGroup taskGroup = selectedTask.getTaskGroup();

		taskRepository.deleteAllByTaskGroupId(taskGroup.getId());
	}

	public void removeAllAfterDate(
			final Long calendarId, final Long selectedTaskId, final Long memberId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);

		Task selectedTask =
				taskRepository
						.findByIdAndCalendarId(selectedTaskId, calendarId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		TaskGroup taskGroup = selectedTask.getTaskGroup();

		taskRepository.deleteAllByTaskGroupIdAndDateAfterOrEqual(
				taskGroup.getId(), selectedTask.getDate());
	}

	public void removeOne(final Long calendarId, final Long selectedTaskId, final Long memberId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);

		Task selectedTask =
				taskRepository
						.findByIdAndCalendarId(selectedTaskId, calendarId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		selectedTask.remove();
	}

	private void addRepeatingTasks(
			final Function<TemporalAmount, Task> temporalToTask, final TaskGroup taskGroup) {

		taskRepository.saveAllBatch(
				taskGroup
						.getRepetitionPattern()
						.orElseThrow(
								() ->
										new RecordNotFoundException(
												ErrorCode.TASK_REPETITION_NOT_FOUND))
						.repeatingStream()
						.map(temporalToTask)
						.toList());
	}

	public void replaceTaskCategoriesWithDefaultCategory(
			Long categoryId, Long calendarId, Long memberId) {
		ScheduleCategory defaultCategory =
				scheduleCategoryQueryService.searchDefaultCategory(calendarId, memberId);
		taskRepository.updateCategorySetDefault(defaultCategory.getId(), categoryId);
	}
}
