package com.sillim.recordit.goal.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
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
		@NotNull Long categoryId,
		Long calendarId,
		Long relatedMonthlyGoalId) {

	public WeeklyGoal toEntity(final ScheduleCategory category, final Calendar calendar) {

		return WeeklyGoal.builder()
				.title(title)
				.description(description)
				.week(week)
				.startDate(startDate)
				.endDate(endDate)
				.category(category)
				.calendar(calendar)
				.build();
	}

	public WeeklyGoal toEntity(
			final ScheduleCategory category,
			final MonthlyGoal relatedMonthlyGoal,
			final Calendar calendar) {

		return WeeklyGoal.builder()
				.title(title)
				.description(description)
				.week(week)
				.startDate(startDate)
				.endDate(endDate)
				.category(category)
				.relatedMonthlyGoal(relatedMonthlyGoal)
				.calendar(calendar)
				.build();
	}
}
