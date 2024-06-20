package com.sillim.recordit.task.dto.response;

import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import java.time.LocalDate;

public record TaskRepetitionDetailsResponse(
		TaskRepetitionType repetitionType,
		Integer repetitionPeriod,
		LocalDate repetitionStartDate,
		LocalDate repetitionEndDate,
		Integer monthOfYear,
		Integer dayOfMonth,
		Integer weekNumber,
		Integer weekday,
		Integer weekdayBit) {

	public static TaskRepetitionDetailsResponse from(TaskRepetitionPattern repetitionPattern) {

		return new TaskRepetitionDetailsResponse(
				repetitionPattern.getRepetitionType(),
				repetitionPattern.getRepetitionPeriod(),
				repetitionPattern.getRepetitionStartDate(),
				repetitionPattern.getRepetitionEndDate(),
				repetitionPattern.getMonthOfYear(),
				repetitionPattern.getDayOfMonth(),
				repetitionPattern.getWeekNumber(),
				repetitionPattern.getWeekday(),
				repetitionPattern.getWeekdayBit());
	}
}
