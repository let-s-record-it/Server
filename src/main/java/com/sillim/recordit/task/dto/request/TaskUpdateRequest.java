package com.sillim.recordit.task.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.validation.common.ColorHexValid;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRemoveStrategy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

public record TaskUpdateRequest(
		@NotNull TaskRemoveStrategy removeStrategy,
		@NotBlank @Length(max = 30) String title,
		@Length(max = 500) String description,
		@NotNull LocalDate date,
		@ColorHexValid String colorHex,
		@NotNull Long calendarId,
		@NotNull Boolean isRepeated,
		@Validated TaskRepetitionUpdateRequest repetition,
		Long relatedMonthlyGoalId,
		Long relatedWeeklyGoalId) {

	public Task toTask(TemporalAmount plusAmount, Calendar calendar, TaskGroup taskGroup) {
		return Task.builder()
				.title(title)
				.description(description)
				.date(date.plus(plusAmount))
				.colorHex(colorHex)
				.calendar(calendar)
				.taskGroup(taskGroup)
				.build();
	}
}
