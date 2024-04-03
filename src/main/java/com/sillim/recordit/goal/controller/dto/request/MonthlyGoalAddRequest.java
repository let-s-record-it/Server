package com.sillim.recordit.goal.controller.dto.request;

import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;

// TODO: validation 추가
public record MonthlyGoalAddRequest(
		String title, String description, Integer year, Integer month, String colorHex) {

	public MonthlyGoal toEntity(Member member) {

		return MonthlyGoal.builder()
				.title(title)
				.description(description)
				.year(year)
				.month(month)
				.colorHex(colorHex)
				.member(member)
				.build();
	}
}
