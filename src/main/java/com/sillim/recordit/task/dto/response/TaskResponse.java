package com.sillim.recordit.task.dto.response;

import com.sillim.recordit.task.domain.Task;
import java.time.LocalDate;

public record TaskResponse(
		Long id, String title, LocalDate date, Long categoryId, String colorHex, boolean achieved) {

	public static TaskResponse from(final Task task) {
		return new TaskResponse(
				task.getId(),
				task.getTitle(),
				task.getDate(),
				task.getCategory().getId(),
				task.getColorHex(),
				task.isAchieved());
	}
}
