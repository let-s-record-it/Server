package com.sillim.recordit.task.repository;

import java.time.LocalDate;

public interface CustomTaskRepository {

	void deleteAllByTaskGroupId(Long taskGroupId, LocalDate date);

	void deleteAllByTaskGroupIdAndAchievedIsFalse(Long taskGroupId, LocalDate date);

	boolean existsByTaskGroupIdAndDateEquals(Long taskGroupId, LocalDate date);
}
