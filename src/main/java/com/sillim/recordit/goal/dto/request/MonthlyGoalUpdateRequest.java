package com.sillim.recordit.goal.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
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
		@NotNull Long categoryId,
		Long calendarId) {

	public MonthlyGoal toEntity(
			final ScheduleCategory category, final Member member, final Calendar calendar) {

		return MonthlyGoal.builder()
				.title(title)
				.description(description)
				.startDate(startDate)
				.endDate(endDate)
				.category(category)
				.member(member)
				.calendar(calendar)
				.build();
	}
}
