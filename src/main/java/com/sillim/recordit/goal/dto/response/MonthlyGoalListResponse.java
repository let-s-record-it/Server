package com.sillim.recordit.goal.dto.response;

import com.sillim.recordit.goal.domain.MonthlyGoal;

public record MonthlyGoalListResponse(Long id, String title, Long categoryId, String colorHex, Boolean achieved,
		Long calendarId) {

	public static MonthlyGoalListResponse from(final MonthlyGoal monthlyGoal) {

		return new MonthlyGoalListResponse(monthlyGoal.getId(), monthlyGoal.getTitle(),
				monthlyGoal.getCategory().getId(), monthlyGoal.getColorHex(), monthlyGoal.isAchieved(),
				monthlyGoal.getCalendar().getId());
	}
}
