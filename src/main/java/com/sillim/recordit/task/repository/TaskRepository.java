package com.sillim.recordit.task.repository;

import com.sillim.recordit.task.domain.Task;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, CustomTaskRepository {

	@Query("select t from Task t where t.calendar.id = :calendarId and t.date = :date")
	List<Task> findAllByCalendarIdAndDate(Long calendarId, LocalDate date);

	@Query(
			"select t from Task t where t.calendar.id = :calendarId and t.taskGroup.id ="
					+ " :taskGroupId")
	List<Task> findAllByCalendarIdAndTaskGroupId(Long calendarId, Long taskGroupId);

	@Query(
			"select t from Task t where t.calendar.id = :calendarId and t.taskGroup.id ="
					+ " :taskGroupId and t.date >= :date")
	List<Task> findAllByCalendarIdAndTaskGroupIdAndDateGreaterThanEqual(
			Long calendarId, Long taskGroupId, LocalDate date);
}
