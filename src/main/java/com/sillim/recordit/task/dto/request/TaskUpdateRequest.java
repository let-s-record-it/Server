package com.sillim.recordit.task.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

public record TaskUpdateRequest(
		@NotBlank @Length(max = 30) String newTitle,
		@Length(max = 500) String newDescription,
		@NotNull LocalDate date,
		@NotNull Long newCategoryId,
		@NotNull Long newCalendarId,
		@NotNull Boolean isRepeated,
		@Validated TaskRepetitionUpdateRequest newRepetition,
		@NotNull TaskGroupUpdateRequest newTaskGroup) {

	public Task toTask(
			TemporalAmount plusAmount,
			ScheduleCategory category,
			Calendar calendar,
			TaskGroup taskGroup) {
		return Task.builder()
				.title(newTitle)
				.description(newDescription)
				.date(newRepetition.repetitionStartDate().plus(plusAmount))
				.category(category)
				.calendar(calendar)
				.taskGroup(taskGroup)
				.build();
	}

	public Task toTask(ScheduleCategory category, Calendar calendar, TaskGroup taskGroup) {
		return Task.builder()
				.title(newTitle)
				.description(newDescription)
				.date(date)
				.category(category)
				.calendar(calendar)
				.taskGroup(taskGroup)
				.build();
	}
}
