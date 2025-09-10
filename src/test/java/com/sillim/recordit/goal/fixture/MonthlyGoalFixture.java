package com.sillim.recordit.goal.fixture;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import java.time.LocalDate;

public enum MonthlyGoalFixture {
	DEFAULT("취뽀하기!", "취업할 때까지 숨 참는다!", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 30)),
	MODIFIED(
			"(수정)취뽀하기!", "(수정)취업할 때까지 숨 참는다!", LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));

	private final String title;
	private final String description;
	private final LocalDate startDate;
	private final LocalDate endDate;

	MonthlyGoalFixture(String title, String description, LocalDate startDate, LocalDate endDate) {
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public MonthlyGoal getWithMember(ScheduleCategory category, Calendar calendar) {

		return MonthlyGoal.builder()
				.title(title)
				.description(description)
				.startDate(startDate)
				.endDate(endDate)
				.category(category)
				.calendar(calendar)
				.build();
	}

	public MonthlyGoal getWithStartDateAndEndDate(
			LocalDate startDate, LocalDate endDate, ScheduleCategory category, Calendar calendar) {

		return MonthlyGoal.builder()
				.title(title)
				.description(description)
				.startDate(startDate)
				.endDate(endDate)
				.category(category)
				.calendar(calendar)
				.build();
	}
}
