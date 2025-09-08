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

public record TaskAddRequest(@NotBlank @Length(max = 30) String title, @Length(max = 500) String description,
		@NotNull LocalDate date, @NotNull Boolean isRepeated, @NotNull Long categoryId,
		@Validated TaskRepetitionUpdateRequest repetition, @NotNull TaskGroupUpdateRequest taskGroup) {

	public Task toTask(TemporalAmount plusAmount, ScheduleCategory category, Calendar calendar, TaskGroup taskGroup) {
		return Task.builder().title(title).description(description).date(date.plus(plusAmount)).category(category)
				.calendar(calendar).taskGroup(taskGroup).build();
	}

	public Task toTask(ScheduleCategory category, Calendar calendar, TaskGroup taskGroup) {
		return Task.builder().title(title).description(description).date(date).category(category).calendar(calendar)
				.taskGroup(taskGroup).build();
	}
}
