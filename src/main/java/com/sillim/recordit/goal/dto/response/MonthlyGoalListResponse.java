package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.MonthlyGoal;

public record MonthlyGoalListResponse(Long id, String title, Boolean achieved) {

	public static MonthlyGoalListResponse from(final MonthlyGoal monthlyGoal) {

		return new MonthlyGoalListResponse(
				monthlyGoal.getId(), monthlyGoal.getTitle(), monthlyGoal.isAchieved());
	}
}
