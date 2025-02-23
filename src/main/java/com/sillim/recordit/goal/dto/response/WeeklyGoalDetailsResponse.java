package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WeeklyGoalDetailsResponse(
		Long id,
		String title,
		String description,
		Integer week,
		LocalDate startDate,
		LocalDate endDate,
		Long categoryId,
		String colorHex,
		RelatedMonthlyGoalResponse relatedMonthlyGoal) {

	public static WeeklyGoalDetailsResponse from(final WeeklyGoal weeklyGoal) {

		if (weeklyGoal.getRelatedMonthlyGoal().isEmpty()) {
			return WeeklyGoalDetailsResponse.builder()
					.id(weeklyGoal.getId())
					.title(weeklyGoal.getTitle())
					.description(weeklyGoal.getDescription())
					.week(weeklyGoal.getWeek())
					.startDate(weeklyGoal.getStartDate())
					.endDate(weeklyGoal.getEndDate())
					.categoryId(weeklyGoal.getCategory().getId())
					.colorHex(weeklyGoal.getColorHex())
					.build();
		}
		return WeeklyGoalDetailsResponse.builder()
				.id(weeklyGoal.getId())
				.title(weeklyGoal.getTitle())
				.description(weeklyGoal.getDescription())
				.week(weeklyGoal.getWeek())
				.startDate(weeklyGoal.getStartDate())
				.endDate(weeklyGoal.getEndDate())
				.categoryId(weeklyGoal.getCategory().getId())
				.colorHex(weeklyGoal.getColorHex())
				.relatedMonthlyGoal(
						RelatedMonthlyGoalResponse.from(weeklyGoal.getRelatedMonthlyGoal().get()))
				.build();
	}
}
