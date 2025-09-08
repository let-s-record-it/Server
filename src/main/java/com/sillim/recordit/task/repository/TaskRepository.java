package com.sillim.recordit.task.repository;

import com.sillim.recordit.task.domain.Task;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, CustomTaskRepository, BatchTaskRepository {

	@Query("select t from Task t left join fetch t.category where t.calendar.id = :calendarId and" + " t.date = :date")
	List<Task> findAllByCalendarIdAndDate(Long calendarId, LocalDate date);

	@Query("select t from Task t left join fetch t.category where t.calendar.id = :calendarId and"
			+ " t.taskGroup.id = :taskGroupId")
	List<Task> findAllByTaskGroupId(Long calendarId, Long taskGroupId);

	@Modifying(clearAutomatically = true)
	@Query("update Task t set t.category.id = :defaultCategoryId where t.category.id = :categoryId")
	int updateCategorySetDefault(@Param("defaultCategoryId") Long defaultCategoryId,
			@Param("categoryId") Long categoryId);
}
