package com.sillim.recordit.task.dto.request;

import jakarta.validation.constraints.NotNull;

public record TaskGroupUpdateRequest(
		@NotNull Boolean isRepeated, Long relatedMonthlyGoalId, Long relatedWeeklyGoalId) {}
