package com.sillim.recordit.goal.controller.dto.response;

import com.sillim.recordit.goal.domain.MonthlyGoal;

public record MonthlyGoalDetailsResponse(
		String title, Integer goalYear, Integer goalMonth, String description, String colorHex) {

	public static MonthlyGoalDetailsResponse from(MonthlyGoal monthlyGoal) {

		return new MonthlyGoalDetailsResponse(
				monthlyGoal.getTitle(),
				monthlyGoal.getGoalYear(),
				monthlyGoal.getGoalMonth(),
				monthlyGoal.getDescription(),
				monthlyGoal.getColorHex());
	}
}
