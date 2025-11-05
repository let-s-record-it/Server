package com.sillim.recordit.task.service;

import com.sillim.recordit.calendar.service.CalendarMemberService;
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

	private final TaskRepository taskRepository;
	private final CalendarMemberService calendarMemberService;

	public List<Task> searchAllByDate(
			final Long calendarId, final LocalDate date, final Long memberId) {
		calendarMemberService.validateCalendarMember(calendarId, memberId);

		return taskRepository.findAllByCalendarIdAndDate(calendarId, date);
	}

	public List<Task> searchTasksInMonth(
			Long calendarId, Long memberId, Integer year, Integer month) {
		calendarMemberService.validateCalendarMember(calendarId, memberId);

		return taskRepository.findTasksInMonth(calendarId, year, month);
	}

	public TaskDetailsResponse searchByIdAndCalendarId(
			final Long taskId, final Long calendarId, final Long memberId) {
		calendarMemberService.validateCalendarMember(calendarId, memberId);
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
