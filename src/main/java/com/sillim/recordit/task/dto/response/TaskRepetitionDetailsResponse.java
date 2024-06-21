package com.sillim.recordit.task.dto.response;

import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import java.time.LocalDate;
import lombok.Builder;

@Builder
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

		return TaskRepetitionDetailsResponse.builder()
				.repetitionType(repetitionPattern.getRepetitionType())
				.repetitionPeriod(repetitionPattern.getRepetitionPeriod())
				.repetitionStartDate(repetitionPattern.getRepetitionStartDate())
				.repetitionEndDate(repetitionPattern.getRepetitionEndDate())
				.monthOfYear(repetitionPattern.getMonthOfYear())
				.dayOfMonth(repetitionPattern.getDayOfMonth())
				.weekNumber(repetitionPattern.getWeekNumber())
				.weekday(repetitionPattern.getWeekday())
				.weekdayBit(repetitionPattern.getWeekdayBit())
				.build();
	}
}
