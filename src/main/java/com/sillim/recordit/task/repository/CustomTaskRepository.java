package com.sillim.recordit.task.repository;

import com.sillim.recordit.task.domain.Task;
import java.time.LocalDate;
import java.util.Optional;

public interface CustomTaskRepository {

	Optional<Task> findByIdAndCalendarId(Long taskId, Long calendarId);

	void deleteAllByTaskGroupId(Long taskGroupId);

	void deleteAllByTaskGroupIdAndDateAfterOrEqual(Long taskGroupId, LocalDate date);
}
