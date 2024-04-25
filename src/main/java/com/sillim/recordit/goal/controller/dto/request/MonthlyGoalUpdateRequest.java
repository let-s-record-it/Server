package com.sillim.recordit.goal.controller.dto.request;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.member.domain.Member;
import java.time.LocalDate;

// TODO: validation 추가
public record MonthlyGoalUpdateRequest(
		String title, String description, LocalDate startDate, LocalDate endDate, String colorHex) {

	public MonthlyGoal toEntity(Member member) {

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
