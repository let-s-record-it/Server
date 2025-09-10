package com.sillim.recordit.goal.fixture;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import java.time.LocalDate;

public enum WeeklyGoalFixture {
	DEFAULT(
			"데이터베이스 3장까지",
			"다 할 때까지 숨 참는다!",
			3,
			LocalDate.of(2024, 8, 11),
			LocalDate.of(2024, 8, 17)),
	MODIFIED(
			"(수정)데이터베이스 3장까지",
			"(수정)다 할 때까지 숨 참는다!",
			4,
			LocalDate.of(2024, 8, 18),
			LocalDate.of(2024, 8, 24));

	private final String title;
	private final String description;
	private final Integer week;
	private final LocalDate startDate;
	private final LocalDate endDate;

	WeeklyGoalFixture(
			String title,
			String description,
			Integer week,
			LocalDate startDate,
			LocalDate endDate) {
		this.title = title;
		this.description = description;
		this.week = week;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public WeeklyGoal getWithMember(ScheduleCategory category, Calendar calendar) {

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

	public WeeklyGoal getWithWeekAndStartDateAndEndDate(
			Integer week,
			LocalDate startDate,
			LocalDate endDate,
			ScheduleCategory category,
			Calendar calendar) {

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
}
