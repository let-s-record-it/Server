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
import java.time.temporal.TemporalAmount;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskCommandService {

	private final TaskGroupService taskGroupService;
	private final CalendarService calendarService;

	private final TaskRepository taskRepository;

	public void addTasks(final TaskAddRequest request, final Long calendarId, final Long memberId) {

		Calendar calendar = calendarService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);

		if (request.isRepeated()) {
			TaskGroup taskGroup =
					taskGroupService.addRepeatingTaskGroup(
							request.taskGroup(), request.repetition(), memberId);
			addRepeatingTasks(
					temporalAmount ->
							taskRepository.save(
									request.toTask(temporalAmount, calendar, taskGroup)),
					taskGroup);
			return;
		}
		TaskGroup taskGroup =
				taskGroupService.addNonRepeatingTaskGroup(request.taskGroup(), memberId);
		taskRepository.save(request.toTask(calendar, taskGroup));
	}

	public void resetTaskGroupAndAddNewTasks(
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
		TaskGroup taskGroup = selectedTask.getTaskGroup();
		taskRepository.deleteAllByTaskGroupId(taskGroup.getId()); // 기존에 있던 Task 모두 삭제

		Calendar newCalendar = calendarService.searchByCalendarId(request.newCalendarId());
		newCalendar.validateAuthenticatedMember(memberId);
		if (request.isRepeated()) {
			TaskGroup newTaskGroup =
					taskGroupService.modifyTaskGroupAndMakeRepeatable(
							taskGroup.getId(),
							request.newTaskGroup(),
							request.newRepetition(),
							memberId);
			addRepeatingTasks(
					temporalAmount ->
							taskRepository.save(
									request.toTask(temporalAmount, newCalendar, newTaskGroup)),
					newTaskGroup);
			return;
		}
		TaskGroup newTaskGroup =
				taskGroupService.modifyTaskGroupAndMakeNonRepeatable(
						taskGroup.getId(), request.newTaskGroup(), memberId);
		taskRepository.save(request.toTask(newCalendar, newTaskGroup));
	}

	public void modifyOne(
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
		TaskGroup taskGroup = selectedTask.getTaskGroup();

		Calendar newCalendar = calendarService.searchByCalendarId(request.newCalendarId());
		newCalendar.validateAuthenticatedMember(memberId);
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
							taskRepository.save(
									request.toTask(temporalAmount, newCalendar, newTaskGroup)),
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
				request.newColorHex(),
				newCalendar,
				newTaskGroup);
	}

	public void removeAll(final Long calendarId, final Long selectedTaskId, final Long memberId) {
		Calendar calendar = calendarService.searchByCalendarId(calendarId);
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
		Calendar calendar = calendarService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);

		Task selectedTask =
				taskRepository
						.findByIdAndCalendarId(selectedTaskId, calendarId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		TaskGroup taskGroup = selectedTask.getTaskGroup();

		taskRepository.deleteAllByTaskGroupIdAndDateAfterOrEqual(
				taskGroup.getId(), selectedTask.getDate());
	}

	private void addRepeatingTasks(
			final Consumer<TemporalAmount> temporalToTask, final TaskGroup taskGroup) {

		taskGroup
				.getRepetitionPattern()
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_REPETITION_NOT_FOUND))
				.repeatingStream()
				.forEach(temporalToTask);
	}
}
