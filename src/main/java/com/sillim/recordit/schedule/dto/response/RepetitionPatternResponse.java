package com.sillim.recordit.schedule.dto.response;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.schedule.domain.vo.DayOfMonth;
import com.sillim.recordit.schedule.domain.vo.MonthOfYear;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RepetitionPatternResponse(
		RepetitionType repetitionType,
		Integer repetitionPeriod,
		LocalDateTime repetitionStartDate,
		LocalDateTime repetitionEndDate,
		Integer monthOfYear,
		Integer dayOfMonth,
		WeekNumber weekNumber,
		Weekday weekday,
		RepeatedWeekday repeatedWeekday) {

	public static RepetitionPatternResponse from(RepetitionPattern repetitionPattern) {
		return RepetitionPatternResponse.builder()
				.repetitionType(repetitionPattern.getRepetitionType())
				.repetitionPeriod(repetitionPattern.getRepetitionPeriod())
				.repetitionStartDate(repetitionPattern.getRepetitionStartDate())
				.repetitionEndDate(repetitionPattern.getRepetitionEndDate())
				.monthOfYear(
						repetitionPattern
								.getMonthOfYear()
								.map(MonthOfYear::getMonthOfYear)
								.orElse(null))
				.dayOfMonth(
						repetitionPattern
								.getDayOfMonth()
								.map(DayOfMonth::getDayOfMonth)
								.orElse(null))
				.weekNumber(repetitionPattern.getWeekNumber().orElse(null))
				.weekday(repetitionPattern.getWeekday().orElse(null))
				.repeatedWeekday(
						repetitionPattern.getWeekdayBit().map(RepeatedWeekday::from).orElse(null))
				.build();
	}
}
