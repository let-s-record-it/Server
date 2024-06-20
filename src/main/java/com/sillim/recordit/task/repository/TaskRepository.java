package com.sillim.recordit.task.repository;

import com.sillim.recordit.task.domain.Task;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("select t from Task t where t.calendar.id = :calendarId and t.date = :date")
	List<Task> findAllByCalendarIdAndDate(Long calendarId, LocalDate date);

	@Query(
			"select t from Task t join fetch t.taskGroup where t.id = :taskId and t.calendar.id ="
					+ " :calendarId")
	Optional<Task> findByIdAndCalendarId(Long taskId, Long calendarId);
}
