package com.sillim.recordit.goal.controller.dto.request;

import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;

// TODO: validation 추가
public record MonthlyGoalAddRequest(
		String title, String description, Integer goalYear, Integer goalMonth, String colorHex) {

	public MonthlyGoal toEntity(Member member) {

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
