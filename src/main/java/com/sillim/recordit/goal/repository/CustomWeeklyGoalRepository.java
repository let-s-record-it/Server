package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import java.util.List;

public interface CustomWeeklyGoalRepository {

	List<WeeklyGoal> findWeeklyGoalInMonth(Integer year, Integer month, Long memberId);
}
