package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import java.util.List;

public record GoalListResponse(List<GoalResponse> monthlyGoals, List<GoalResponse> weeklyGoals) {

	public static GoalListResponse of(
			final List<MonthlyGoal> monthlyGoals, final List<WeeklyGoal> weeklyGoals) {
		List<GoalResponse> monthlyGoalResponses =
				monthlyGoals.stream().map(GoalResponse::from).toList();
		List<GoalResponse> weeklyGoalResponses =
				weeklyGoals.stream().map(GoalResponse::from).toList();
		return new GoalListResponse(monthlyGoalResponses, weeklyGoalResponses);
	}
}
