package com.sillim.recordit.goal.fixture;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.member.domain.Member;
import java.time.LocalDate;

public enum MonthlyGoalFixture {
	DEFAULT(
			"취뽀하기!",
			"취업할 때까지 숨 참는다!",
			LocalDate.of(2024, 4, 1),
			LocalDate.of(2024, 4, 30),
			"ff83c8ef"),
	MODIFIED(
			"(수정)취뽀하기!",
			"(수정)취업할 때까지 숨 참는다!",
			LocalDate.of(2024, 5, 1),
			LocalDate.of(2024, 5, 31),
			"ff123456");

	private final String title;
	private final String description;
	private final LocalDate startDate;
	private final LocalDate endDate;
	private final String colorHex;

	MonthlyGoalFixture(
			String title,
			String description,
			LocalDate startDate,
			LocalDate endDate,
			String colorHex) {
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.colorHex = colorHex;
	}

	public MonthlyGoal getWithMember(Member member) {

		return MonthlyGoal.builder()
				.title(title)
				.description(description)
				.startDate(startDate)
				.endDate(endDate)
				.colorHex(colorHex)
				.member(member)
				.build();
	}

	public MonthlyGoal getWithStartDateAndEndDate(
			LocalDate startDate, LocalDate endDate, Member member) {

		return MonthlyGoal.builder()
				.title(title)
				.description(description)
				.startDate(startDate)
				.endDate(endDate)
				.colorHex(colorHex)
				.member(member)
				.build();
	}
}
