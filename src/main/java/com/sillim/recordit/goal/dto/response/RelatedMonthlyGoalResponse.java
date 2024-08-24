package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.MonthlyGoal;

public record RelatedMonthlyGoalResponse(Long id, String title) {

	public static RelatedMonthlyGoalResponse from(MonthlyGoal monthlyGoal) {
		return new RelatedMonthlyGoalResponse(monthlyGoal.getId(), monthlyGoal.getTitle());
	}
}
