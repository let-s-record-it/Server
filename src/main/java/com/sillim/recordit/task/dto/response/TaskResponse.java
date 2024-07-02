package com.sillim.recordit.task.dto.response;

import com.sillim.recordit.task.domain.Task;

public record TaskResponse(Long id, String title, boolean achieved) {

	public static TaskResponse from(final Task task) {
		return new TaskResponse(task.getId(), task.getTitle(), task.isAchieved());
	}
}
