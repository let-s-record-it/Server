package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import java.util.List;

public record WeeklyGoalListResponse(Integer week, List<WeeklyGoalResponse> weeklyGoals) {

	public static WeeklyGoalListResponse from(Integer week, List<WeeklyGoal> weeklyGoals) {

		return new WeeklyGoalListResponse(
				week, weeklyGoals.stream().map(WeeklyGoalResponse::from).toList());
	}
}
