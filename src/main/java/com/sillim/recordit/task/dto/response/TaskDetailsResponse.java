package com.sillim.recordit.task.dto.response;

import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TaskDetailsResponse(
		Long taskId,
		String title,
		String description,
		LocalDate date,
		String colorHex,
		Long calendarId,
		String calendarTitle,
		Boolean isRepeated,
		TaskRepetitionDetailsResponse repetition,
		RelatedGoalDetailsResponse relatedMonthlyGoal,
		RelatedGoalDetailsResponse relatedWeeklyGoal) {

	public static TaskDetailsResponse from(final Task task) {
		return TaskDetailsResponse.builder()
				.taskId(task.getId())
				.title(task.getTitle())
				.description(task.getDescription())
				.date(task.getDate())
				.colorHex(task.getColorHex())
				.calendarId(task.getCalendar().getId())
				.calendarTitle(task.getCalendar().getTitle())
				.isRepeated(false)
				.repetition(null)
				.relatedMonthlyGoal(
						task.getTaskGroup()
								.getMonthlyGoal()
								.map(RelatedGoalDetailsResponse::from)
								.orElse(null))
				.relatedWeeklyGoal(
						task.getTaskGroup()
								.getWeeklyGoal()
								.map(RelatedGoalDetailsResponse::from)
								.orElse(null))
				.build();
	}

	public static TaskDetailsResponse of(
			final Task task, final TaskRepetitionPattern repetitionPattern) {
		return TaskDetailsResponse.builder()
				.taskId(task.getId())
				.title(task.getTitle())
				.description(task.getDescription())
				.date(task.getDate())
				.colorHex(task.getColorHex())
				.calendarId(task.getCalendar().getId())
				.calendarTitle(task.getCalendar().getTitle())
				.isRepeated(true)
				.repetition(TaskRepetitionDetailsResponse.from(repetitionPattern))
				.relatedMonthlyGoal(
						task.getTaskGroup()
								.getMonthlyGoal()
								.map(RelatedGoalDetailsResponse::from)
								.orElse(null))
				.relatedWeeklyGoal(
						task.getTaskGroup()
								.getWeeklyGoal()
								.map(RelatedGoalDetailsResponse::from)
								.orElse(null))
				.build();
	}
}
