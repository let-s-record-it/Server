package com.sillim.recordit.task.dto.response;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
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
				.monthOfYear(repetitionPattern.getMonthOfYear().orElse(null))
				.dayOfMonth(repetitionPattern.getDayOfMonth().orElse(null))
				.weekNumber(
						repetitionPattern.getWeekNumber().map(WeekNumber::getValue).orElse(null))
				.weekday(repetitionPattern.getWeekday().map(Weekday::getValue).orElse(null))
				.weekdayBit(repetitionPattern.getWeekdayBit().orElse(null))
				.build();
	}
}
