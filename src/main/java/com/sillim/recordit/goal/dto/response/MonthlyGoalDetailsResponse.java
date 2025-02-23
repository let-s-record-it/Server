package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import java.time.LocalDate;

public record MonthlyGoalDetailsResponse(
		Long id,
		String title,
		String description,
		LocalDate startDate,
		LocalDate endDate,
		Long categoryId,
		String colorHex) {

	public static MonthlyGoalDetailsResponse from(final MonthlyGoal monthlyGoal) {

		return new MonthlyGoalDetailsResponse(
				monthlyGoal.getId(),
				monthlyGoal.getTitle(),
				monthlyGoal.getDescription(),
				monthlyGoal.getStartDate(),
				monthlyGoal.getEndDate(),
				monthlyGoal.getCategory().getId(),
				monthlyGoal.getColorHex());
	}
}
