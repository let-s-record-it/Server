package com.sillim.recordit.task.service;

import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.task.domain.Task;
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
			final LocalDate date, final Long calendarId, final Long memberId) {

		calendarService.validateOwnerOfCalendar(calendarId, memberId);

		return taskRepository.findAllByCalendarIdAndDate(calendarId, date);
	}
}
