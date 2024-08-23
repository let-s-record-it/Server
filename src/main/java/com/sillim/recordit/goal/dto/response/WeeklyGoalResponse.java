package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.WeeklyGoal;

public record WeeklyGoalResponse(
		Long id, String title, Boolean achieved, RelatedMonthlyGoalResponse relatedMonthlyGoal) {

	public static WeeklyGoalResponse from(final WeeklyGoal weeklyGoal) {

		if (weeklyGoal.getRelatedMonthlyGoal().isEmpty()) {
			return new WeeklyGoalResponse(
					weeklyGoal.getId(), weeklyGoal.getTitle(), weeklyGoal.isAchieved(), null);
		}
		return new WeeklyGoalResponse(
				weeklyGoal.getId(),
				weeklyGoal.getTitle(),
				weeklyGoal.isAchieved(),
				RelatedMonthlyGoalResponse.from(weeklyGoal.getRelatedMonthlyGoal().get()));
	}
}
