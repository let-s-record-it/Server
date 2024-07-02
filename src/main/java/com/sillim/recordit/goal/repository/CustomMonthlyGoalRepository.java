package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import java.util.List;

public interface CustomMonthlyGoalRepository {

	List<MonthlyGoal> findMonthlyGoalInMonth(Integer year, Integer month, Long memberId);
}
