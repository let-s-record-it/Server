package com.sillim.recordit.task.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.validation.common.ColorHexValid;
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
		@ColorHexValid String newColorHex,
		@NotNull Long newCalendarId,
		@NotNull Boolean isRepeated,
		@Validated TaskRepetitionUpdateRequest newRepetition,
		TaskGroupUpdateRequest newTaskGroup) {

	public Task toTask(TemporalAmount plusAmount, Calendar calendar, TaskGroup taskGroup) {
		return Task.builder()
				.title(newTitle)
				.description(newDescription)
				.date(date.plus(plusAmount))
				.colorHex(newColorHex)
				.calendar(calendar)
				.taskGroup(taskGroup)
				.build();
	}

	public Task toTask(Calendar calendar, TaskGroup taskGroup) {
		return Task.builder()
				.title(newTitle)
				.description(newDescription)
				.date(date)
				.colorHex(newColorHex)
				.calendar(calendar)
				.taskGroup(taskGroup)
				.build();
	}
}
