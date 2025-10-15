package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomWeeklyGoalRepository {

	List<WeeklyGoal> findWeeklyGoalInMonth(Integer year, Integer month, Long calendarId);

	@Query(
			"select wg from WeeklyGoal wg left join fetch wg.calendar left join fetch wg.category left join fetch wg.relatedMonthlyGoal where wg.id = :id")
	Optional<WeeklyGoal> findWeeklyGoalById(@Param("id") Long id);
}
