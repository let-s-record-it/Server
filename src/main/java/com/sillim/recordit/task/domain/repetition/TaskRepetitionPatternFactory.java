package com.sillim.recordit.task.domain.repetition;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import java.time.LocalDate;
import java.util.Objects;

public class TaskRepetitionPatternFactory {

	public static TaskRepetitionPattern create(final TaskRepetitionType repetitionType, final Integer repetitionPeriod,
			final LocalDate repetitionStartDate, final LocalDate repetitionEndDate, final Integer monthOfYear,
			final Integer dayOfMonth, final WeekNumber weekNumber, final Weekday weekday, final Integer weekdayBit,
			final TaskGroup taskGroup) {
		validateRepetitionType(repetitionType);
		return switch (repetitionType) {
			case DAILY -> TaskDailyRepetitionPattern.createDaily(repetitionPeriod, repetitionStartDate,
					repetitionEndDate, taskGroup);
			case WEEKLY -> TaskWeeklyRepetitionPattern.createWeekly(repetitionPeriod, repetitionStartDate,
					repetitionEndDate, weekdayBit, taskGroup);
			case MONTHLY_WITH_DATE -> TaskMonthlyRepetitionPattern.createMonthlyWithDate(repetitionPeriod,
					repetitionStartDate, repetitionEndDate, dayOfMonth, taskGroup);
			case MONTHLY_WITH_WEEKDAY -> TaskMonthlyRepetitionPattern.createMonthlyWithWeekday(repetitionPeriod,
					repetitionStartDate, repetitionEndDate, weekNumber, weekday, taskGroup);
			case MONTHLY_WITH_LAST_DAY -> TaskMonthlyRepetitionPattern.createMonthlyWithLastDay(repetitionPeriod,
					repetitionStartDate, repetitionEndDate, taskGroup);
			case YEARLY_WITH_DATE -> TaskYearlyRepetitionPattern.createYearlyWithDate(repetitionPeriod,
					repetitionStartDate, repetitionEndDate, monthOfYear, dayOfMonth, taskGroup);
			case YEARLY_WITH_WEEKDAY -> TaskYearlyRepetitionPattern.createYearlyWithWeekday(repetitionPeriod,
					repetitionStartDate, repetitionEndDate, monthOfYear, weekNumber, weekday, taskGroup);
			case YEARLY_WITH_LAST_DAY -> TaskYearlyRepetitionPattern.createYearlyWithLastDay(repetitionPeriod,
					repetitionStartDate, repetitionEndDate, monthOfYear, taskGroup);
		};
	}

	private static void validateRepetitionType(final TaskRepetitionType repetitionType) {
		if (Objects.isNull(repetitionType)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_TYPE);
		}
	}
}
