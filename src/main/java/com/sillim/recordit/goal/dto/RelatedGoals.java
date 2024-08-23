package com.sillim.recordit.goal.dto;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;

public record RelatedGoals(MonthlyGoal monthlyGoal, WeeklyGoal weeklyGoal) {

	public static RelatedGoals empty() {
		return new RelatedGoals(null, null);
	}

	public static RelatedGoals from(MonthlyGoal monthlyGoal) {
		return new RelatedGoals(monthlyGoal, null);
	}

	public static RelatedGoals from(WeeklyGoal weeklyGoal) {
		return new RelatedGoals(null, weeklyGoal);
	}

	public static RelatedGoals of(MonthlyGoal monthlyGoal, WeeklyGoal weeklyGoal) {
		return new RelatedGoals(monthlyGoal, weeklyGoal);
	}
}
