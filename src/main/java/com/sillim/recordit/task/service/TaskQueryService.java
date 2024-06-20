package com.sillim.recordit.task.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.dto.response.TaskDetailsResponse;
import com.sillim.recordit.task.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskQueryService {

	private final CalendarService calendarService;
	private final TaskRepetitionPatternService repetitionPatternService;
	private final TaskRepository taskRepository;

	public List<Task> searchAllByDate(
			final Long calendarId, final LocalDate date, final Long memberId) {

		final Calendar calendar = calendarService.searchByCalendarId(calendarId, memberId);

		return taskRepository.findAllByCalendarIdAndDate(calendar.getId(), date);
	}

	public TaskDetailsResponse searchByIdAndCalendarId(
			final Long taskId, final Long calendarId, final Long memberId) {

		final Calendar calendar = calendarService.searchByCalendarId(calendarId, memberId);
		final Task task =
				taskRepository
						.findByIdAndCalendarId(taskId, calendar.getId())
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		if (task.isRepeated()) {
			Long taskGroupId = task.getTaskGroup().getId();
			return TaskDetailsResponse.of(
					task, repetitionPatternService.searchByTaskGroupId(taskGroupId));
		}
		return TaskDetailsResponse.from(task);
	}
}
