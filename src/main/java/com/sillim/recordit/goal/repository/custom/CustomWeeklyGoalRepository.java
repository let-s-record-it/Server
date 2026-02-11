package com.sillim.recordit.goal.repository.custom;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import java.util.List;
import java.util.Optional;

public interface CustomWeeklyGoalRepository {

	List<WeeklyGoal> findWeeklyGoalInMonth(Integer year, Integer month, Long calendarId);

	Optional<WeeklyGoal> findWeeklyGoal(Long id);
}
