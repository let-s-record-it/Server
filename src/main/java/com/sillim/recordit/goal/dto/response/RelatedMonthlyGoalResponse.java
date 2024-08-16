package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;

public record RelatedMonthlyGoalResponse(Long id, String title) {

	public static RelatedMonthlyGoalResponse from(MonthlyGoal monthlyGoal) {
		return new RelatedMonthlyGoalResponse(monthlyGoal.getId(), monthlyGoal.getTitle());
	}

	public static RelatedMonthlyGoalResponse from(WeeklyGoal weeklyGoal) {
		return new RelatedMonthlyGoalResponse(weeklyGoal.getId(), weeklyGoal.getTitle());
	}
}
