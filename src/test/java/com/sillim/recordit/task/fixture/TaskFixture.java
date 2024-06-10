package com.sillim.recordit.task.fixture;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import java.time.LocalDate;

public enum TaskFixture {
	DEFAULT("회의록 작성", "프로젝트 회의록 작성", LocalDate.of(2024, 6, 1), "ff40d974"),
	;

	private final String title;
	private final String description;
	private final LocalDate date;
	private final String colorHex;

	TaskFixture(String title, String description, LocalDate date, String colorHex) {
		this.title = title;
		this.description = description;
		this.date = date;
		this.colorHex = colorHex;
	}

	public Task get(Calendar calendar, TaskGroup taskGroup) {

		return Task.builder()
				.title(title)
				.description(description)
				.date(date)
				.colorHex(colorHex)
				.calendar(calendar)
				.taskGroup(taskGroup)
				.build();
	}
}
