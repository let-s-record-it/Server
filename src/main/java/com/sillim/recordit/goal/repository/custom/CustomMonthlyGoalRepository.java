package com.sillim.recordit.goal.repository.custom;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import java.util.List;
import java.util.Optional;

public interface CustomMonthlyGoalRepository {

	Optional<MonthlyGoal> findMonthlyGoal(Long monthlyGoalId);

	List<MonthlyGoal> findMonthlyGoalInMonth(Integer year, Integer month, Long calendarId);
}
