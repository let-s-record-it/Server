package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import lombok.Builder;

@Builder
public record WeeklyGoalResponse(
		Long id, String title, Boolean achieved, RelatedMonthlyGoalResponse relatedMonthlyGoal) {

	public static WeeklyGoalResponse from(final WeeklyGoal weeklyGoal) {

		if (weeklyGoal.getRelatedMonthlyGoal().isEmpty()) {
			return WeeklyGoalResponse.builder()
					.id(weeklyGoal.getId())
					.title(weeklyGoal.getTitle())
					.achieved(weeklyGoal.isAchieved())
					.build();
		}
		return WeeklyGoalResponse.builder()
				.id(weeklyGoal.getId())
				.title(weeklyGoal.getTitle())
				.achieved(weeklyGoal.isAchieved())
				.relatedMonthlyGoal(
						RelatedMonthlyGoalResponse.from(weeklyGoal.getRelatedMonthlyGoal().get()))
				.build();
	}
}
