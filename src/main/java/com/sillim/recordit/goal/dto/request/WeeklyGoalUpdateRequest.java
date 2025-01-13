package com.sillim.recordit.goal.dto.request;

import com.sillim.recordit.global.validation.common.ColorHexValid;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

public record WeeklyGoalUpdateRequest(
		@NotBlank @Length(max = 30) String title,
		@Length(max = 500) String description,
		@NotNull @Range(min = 1, max = 6) Integer week,
		@NotNull LocalDate startDate,
		@NotNull LocalDate endDate,
		@ColorHexValid String colorHex,
		Long relatedMonthlyGoalId) {

	public WeeklyGoal toEntity(final Member member) {

		return WeeklyGoal.builder()
				.title(title)
				.description(description)
				.week(week)
				.startDate(startDate)
				.endDate(endDate)
				.colorHex(colorHex)
				.member(member)
				.build();
	}

	public WeeklyGoal toEntity(final MonthlyGoal relatedMonthlyGoal, final Member member) {

		return WeeklyGoal.builder()
				.title(title)
				.description(description)
				.week(week)
				.startDate(startDate)
				.endDate(endDate)
				.colorHex(colorHex)
				.relatedMonthlyGoal(relatedMonthlyGoal)
				.member(member)
				.build();
	}
}
