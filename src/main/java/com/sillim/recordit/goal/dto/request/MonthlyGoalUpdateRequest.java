package com.sillim.recordit.goal.dto.request;

import com.sillim.recordit.global.validation.common.ColorHexValid;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;

public record MonthlyGoalUpdateRequest(
		@NotBlank @Length(max = 30) String title,
		@Length(max = 500) String description,
		@NotNull LocalDate startDate,
		@NotNull LocalDate endDate,
		@ColorHexValid String colorHex) {

	public MonthlyGoal toEntity(final Member member) {

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
