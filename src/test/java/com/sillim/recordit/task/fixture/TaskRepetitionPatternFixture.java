package com.sillim.recordit.task.fixture;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPatternFactory;
import java.time.LocalDate;

public enum TaskRepetitionPatternFixture {
	DAILY(
			TaskRepetitionType.DAILY,
			1,
			LocalDate.of(2024, 1, 1),
			LocalDate.of(2024, 3, 31),
			null,
			null,
			null,
			null,
			null),
	WEEKLY(
			TaskRepetitionType.WEEKLY,
			1,
			LocalDate.of(2024, 1, 1),
			LocalDate.of(2024, 3, 31),
			null,
			null,
			null,
			null,
			Integer.parseInt("1110100", 2)), // 수 금 토 일
	MONTHLY_WITH_DATE(
			TaskRepetitionType.MONTHLY_WITH_DATE,
			1,
			LocalDate.of(2024, 1, 12),
			LocalDate.of(2024, 3, 31),
			null,
			12,
			null,
			null,
			null),
	MONTHLY_WITH_WEEKDAY(
			TaskRepetitionType.MONTHLY_WITH_WEEKDAY,
			1,
			LocalDate.of(2024, 1, 12),
			LocalDate.of(2024, 3, 31),
			null,
			null,
			WeekNumber.SECOND,
			Weekday.FRI,
			null),
	MONTHLY_WITH_LAST_DAY(
			TaskRepetitionType.MONTHLY_WITH_LAST_DAY,
			1,
			LocalDate.of(2024, 1, 31),
			LocalDate.of(2024, 3, 31),
			null,
			null,
			null,
			null,
			null),
	YEARLY_WITH_DATE(
			TaskRepetitionType.YEARLY_WITH_DATE,
			1,
			LocalDate.of(2024, 2, 12),
			LocalDate.of(2025, 3, 31),
			2,
			12,
			null,
			null,
			null),
	YEARLY_WITH_WEEKDAY(
			TaskRepetitionType.YEARLY_WITH_WEEKDAY,
			1,
			LocalDate.of(2024, 2, 12),
			LocalDate.of(2025, 3, 31),
			2,
			null,
			WeekNumber.SECOND,
			Weekday.MON,
			null),
	YEARLY_WITH_LAST_DAY(
			TaskRepetitionType.YEARLY_WITH_LAST_DAY,
			1,
			LocalDate.of(2024, 2, 29),
			LocalDate.of(2025, 3, 31),
			2,
			null,
			null,
			null,
			null),
	;

	private final TaskRepetitionType repetitionType;
	private final Integer repetitionPeriod;
	private final LocalDate repetitionStartDate;
	private final LocalDate repetitionEndDate;
	private final Integer monthOfYear;
	private final Integer dayOfMonth;
	private final WeekNumber weekNumber;
	private final Weekday weekday;
	private final Integer weekdayBit;

	TaskRepetitionPatternFixture(
			TaskRepetitionType repetitionType,
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			Integer monthOfYear,
			Integer dayOfMonth,
			WeekNumber weekNumber,
			Weekday weekday,
			Integer weekdayBit) {
		this.repetitionType = repetitionType;
		this.repetitionPeriod = repetitionPeriod;
		this.repetitionStartDate = repetitionStartDate;
		this.repetitionEndDate = repetitionEndDate;
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		this.weekNumber = weekNumber;
		this.weekday = weekday;
		this.weekdayBit = weekdayBit;
	}

	public TaskRepetitionPattern get(TaskGroup taskGroup) {
		return TaskRepetitionPatternFactory.create(
				repetitionType,
				repetitionPeriod,
				repetitionStartDate,
				repetitionEndDate,
				monthOfYear,
				dayOfMonth,
				weekNumber,
				weekday,
				weekdayBit,
				taskGroup);
	}
}
