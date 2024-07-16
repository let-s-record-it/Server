package com.sillim.recordit.task.repository;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.task.domain.Task;
import java.time.LocalDate;
import java.util.Optional;

public interface CustomTaskRepository {

	Optional<Task> findByIdAndCalendarId(Long taskId, Long calendarId);

	void updateAllByCalendarIdAndTaskGroupId(
			Long calendarId,
			Long taskGroupId,
			String newTitle,
			String newDescription,
			LocalDate date,
			String colorHex,
			Calendar newCalendar);
}
