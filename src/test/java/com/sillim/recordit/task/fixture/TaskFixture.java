package com.sillim.recordit.task.fixture;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import java.time.LocalDate;

public enum TaskFixture {
	DEFAULT("회의록 작성", "프로젝트 회의록 작성", LocalDate.of(2024, 6, 1)),
	;

	private final String title;
	private final String description;
	private final LocalDate date;

	TaskFixture(String title, String description, LocalDate date) {
		this.title = title;
		this.description = description;
		this.date = date;
	}

	public Task get(ScheduleCategory category, Calendar calendar, TaskGroup taskGroup) {
		return Task.builder()
				.title(title)
				.description(description)
				.date(date)
				.category(category)
				.calendar(calendar)
				.taskGroup(taskGroup)
				.build();
	}

	public Task getWithDate(
			LocalDate date, ScheduleCategory category, Calendar calendar, TaskGroup taskGroup) {
		return Task.builder()
				.title(title)
				.description(description)
				.date(date)
				.category(category)
				.calendar(calendar)
				.taskGroup(taskGroup)
				.build();
	}
}
