package com.sillim.recordit.task.dto.response;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;

public record RelatedGoalDetailsResponse(Long id, String title) {

	public static RelatedGoalDetailsResponse from(MonthlyGoal monthlyGoal) {
		return new RelatedGoalDetailsResponse(monthlyGoal.getId(), monthlyGoal.getTitle());
	}

	public static RelatedGoalDetailsResponse from(WeeklyGoal weeklyGoal) {
		return new RelatedGoalDetailsResponse(weeklyGoal.getId(), weeklyGoal.getTitle());
	}
}
