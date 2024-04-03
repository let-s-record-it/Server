package com.sillim.recordit.goal.fixture;

import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;

public enum MonthlyGoalFixture {
	MONTHLY_GOAL("취뽀하기!", "취업할 때까지 숨 참는다!", 2024, 5, "#83c8ef");

	private String title;
	private String description;
	private Integer goalYear;
	private Integer goalMonth;
	private String colorHex;

	MonthlyGoalFixture(
			String title,
			String description,
			Integer goalYear,
			Integer goalMonth,
			String colorHex) {
		this.title = title;
		this.description = description;
		this.goalYear = goalYear;
		this.goalMonth = goalMonth;
		this.colorHex = colorHex;
	}

	public MonthlyGoal getWithMember(Member member) {

		return MonthlyGoal.builder()
				.title(title)
				.description(description)
				.goalYear(goalYear)
				.goalMonth(goalMonth)
				.colorHex(colorHex)
				.member(member)
				.build();
	}
}
