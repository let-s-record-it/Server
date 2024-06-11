package com.sillim.recordit.task.repository;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.task.domain.Task;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findAllByCalendarAndDate(Calendar calendar, LocalDate date);
}
