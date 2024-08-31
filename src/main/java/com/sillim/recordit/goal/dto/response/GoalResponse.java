package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;

public record GoalResponse(Long id, String title) {

	public static GoalResponse from(final MonthlyGoal monthlyGoal) {
		return new GoalResponse(monthlyGoal.getId(), monthlyGoal.getTitle());
	}

	public static GoalResponse from(final WeeklyGoal weeklyGoal) {
		return new GoalResponse(weeklyGoal.getId(), weeklyGoal.getTitle());
	}
}
