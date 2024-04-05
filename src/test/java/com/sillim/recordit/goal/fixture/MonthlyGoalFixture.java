package com.sillim.recordit.goal.fixture;

import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;

public enum MonthlyGoalFixture {
	DEFAULT("취뽀하기!", "취업할 때까지 숨 참는다!", 2024, 5, "#83c8ef"),
	MODIFIED("(수정)취뽀하기!", "(수정)취업할 때까지 숨 참는다!", 2024, 12, "#123456");

	private final String title;
	private final String description;
	private final Integer goalYear;
	private final Integer goalMonth;
	private final String colorHex;

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
