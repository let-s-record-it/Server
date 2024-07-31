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
	private final TaskRepository taskRepository;

	public List<Task> searchAllByDate(
			final Long calendarId, final LocalDate date, final Long memberId) {

		Calendar calendar = calendarService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);

		return taskRepository.findAllByCalendarIdAndDate(calendarId, date);
	}

	public TaskDetailsResponse searchByIdAndCalendarId(
			final Long taskId, final Long calendarId, final Long memberId) {

		Calendar calendar = calendarService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);
		Task task =
				taskRepository
						.findByIdAndCalendarId(taskId, calendarId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));
		if (task.isRepeated()) {
			return TaskDetailsResponse.of(
					task,
					task.getTaskGroup()
							.getRepetitionPattern()
							.orElseThrow(
									() ->
											new RecordNotFoundException(
													ErrorCode.TASK_REPETITION_NOT_FOUND)));
		}
		return TaskDetailsResponse.from(task);
	}
}
