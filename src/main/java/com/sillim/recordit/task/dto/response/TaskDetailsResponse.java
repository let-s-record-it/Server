package com.sillim.recordit.task.dto.response;

import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import java.time.LocalDate;

public record TaskDetailsResponse(
		Long taskId,
		String title,
		String description,
		LocalDate date,
		String colorHex,
		Boolean isRepeated,
		TaskRepetitionDetailsResponse repetition,
		Long relatedMonthlyGoalId,
		Long relatedWeeklyGoalId) {

	public static TaskDetailsResponse from(final Task task) {
		return new TaskDetailsResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getDate(),
				task.getColorHex(),
				false,
				null,
				task.getMonthlyGoalId(),
				task.getWeeklyGoalId());
	}

	public static TaskDetailsResponse of(
			final Task task, final TaskRepetitionPattern repetitionPattern) {
		return new TaskDetailsResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getDate(),
				task.getColorHex(),
				true,
				TaskRepetitionDetailsResponse.from(repetitionPattern),
				task.getMonthlyGoalId(),
				task.getWeeklyGoalId());
	}
}
