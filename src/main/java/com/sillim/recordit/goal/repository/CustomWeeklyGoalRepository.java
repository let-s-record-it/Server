package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import java.util.List;
import java.util.Optional;

public interface CustomWeeklyGoalRepository {

	List<WeeklyGoal> findWeeklyGoalInMonth(Integer year, Integer month, Long memberId);

	Optional<WeeklyGoal> findWeeklyGoalById(Long id);
}
