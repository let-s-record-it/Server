package com.sillim.recordit.goal.controller.dto.response;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import java.time.LocalDate;

public record MonthlyGoalDetailsResponse(
		Long id,
		String title,
		LocalDate startDate,
		LocalDate endDate,
		String description,
		String colorHex) {

	public static MonthlyGoalDetailsResponse from(MonthlyGoal monthlyGoal) {

		return new MonthlyGoalDetailsResponse(
				monthlyGoal.getId(),
				monthlyGoal.getTitle(),
				monthlyGoal.getStartDate(),
				monthlyGoal.getEndDate(),
				monthlyGoal.getDescription(),
				monthlyGoal.getColorHex());
	}
}
