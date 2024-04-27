package com.sillim.recordit.schedule.fixture;

import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.domain.WeekNumber;
import com.sillim.recordit.schedule.domain.Weekday;
import java.time.LocalDateTime;

public enum RepetitionPatternFixture {
	START_IS_AFTER_END(
			RepetitionType.DAILY,
			1,
			LocalDateTime.of(2024, 2, 1, 0, 0),
			LocalDateTime.of(2024, 1, 31, 0, 0),
			null,
			null,
			null,
			null,
			null),
	PERIOD_1_UNDER(
			RepetitionType.DAILY,
			0,
			LocalDateTime.of(2024, 1, 1, 0, 0),
			LocalDateTime.of(2024, 3, 31, 0, 0),
			null,
			null,
			null,
			null,
			null),
	PERIOD_999_OVER(
			RepetitionType.DAILY,
			1000,
			LocalDateTime.of(2024, 1, 1, 0, 0),
			LocalDateTime.of(2024, 3, 31, 0, 0),
			null,
			null,
			null,
			null,
			null),
	DAILY(
			RepetitionType.DAILY,
			1,
			LocalDateTime.of(2024, 1, 1, 0, 0),
			LocalDateTime.of(2024, 3, 31, 0, 0),
			null,
			null,
			null,
			null,
			null),
	WEEKLY(
			RepetitionType.WEEKLY,
			1,
			LocalDateTime.of(2024, 1, 1, 0, 0),
			LocalDateTime.of(2024, 3, 31, 0, 0),
			null,
			null,
			null,
			null,
			Integer.parseInt("1110100", 2)), // 수 금 토 일
	MONTHLY_WITH_DATE(
			RepetitionType.MONTHLY_WITH_DATE,
			1,
			LocalDateTime.of(2024, 1, 12, 0, 0),
			LocalDateTime.of(2024, 3, 31, 0, 0),
			null,
			12,
			null,
			null,
			null),
	MONTHLY_WITH_WEEKDAY(
			RepetitionType.MONTHLY_WITH_WEEKDAY,
			1,
			LocalDateTime.of(2024, 1, 12, 0, 0),
			LocalDateTime.of(2024, 3, 31, 0, 0),
			null,
			null,
			WeekNumber.SECOND,
			Weekday.FRI,
			null),
	MONTHLY_WITH_LAST_DAY(
			RepetitionType.MONTHLY_WITH_LAST_DAY,
			1,
			LocalDateTime.of(2024, 1, 31, 0, 0),
			LocalDateTime.of(2024, 3, 31, 0, 0),
			null,
			null,
			null,
			null,
			null),
	YEARLY_WITH_DATE(
			RepetitionType.YEARLY_WITH_DATE,
			1,
			LocalDateTime.of(2024, 2, 12, 0, 0),
			LocalDateTime.of(2025, 3, 31, 0, 0),
			2,
			12,
			null,
			null,
			null),
	YEARLY_WITH_WEEKDAY(
			RepetitionType.YEARLY_WITH_WEEKDAY,
			1,
			LocalDateTime.of(2024, 2, 12, 0, 0),
			LocalDateTime.of(2025, 3, 31, 0, 0),
			2,
			null,
			WeekNumber.SECOND,
			Weekday.MON,
			null),
	YEARLY_WITH_LAST_DAY(
			RepetitionType.YEARLY_WITH_LAST_DAY,
			1,
			LocalDateTime.of(2024, 2, 29, 0, 0),
			LocalDateTime.of(2025, 3, 31, 0, 0),
			2,
			null,
			null,
			null,
			null),
	NOT_EQUAL_DAY_OF_MONTH(
			RepetitionType.YEARLY_WITH_DATE,
			1,
			LocalDateTime.of(2024, 2, 12, 0, 0),
			LocalDateTime.of(2025, 3, 31, 0, 0),
			2,
			14,
			null,
			null,
			null),
	NOT_EQUAL_MONTH_OF_YEAR(
			RepetitionType.YEARLY_WITH_LAST_DAY,
			1,
			LocalDateTime.of(2024, 2, 29, 0, 0),
			LocalDateTime.of(2025, 3, 31, 0, 0),
			3,
			null,
			null,
			null,
			null),
	;

	private final RepetitionType repetitionType;
	private final Integer repetitionPeriod;
	private final LocalDateTime repetitionStartDate;
	private final LocalDateTime repetitionEndDate;
	private final Integer monthOfYear;
	private final Integer dayOfMonth;
	private final WeekNumber weekNumber;
	private final Weekday weekday;
	private final Integer weekdayBit;

	RepetitionPatternFixture(
			RepetitionType repetitionType,
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
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

	public RepetitionPattern getRepetitionPattern(ScheduleGroup scheduleGroup) {
		return RepetitionPattern.create(
				repetitionType,
				repetitionPeriod,
				repetitionStartDate,
				repetitionEndDate,
				monthOfYear,
				dayOfMonth,
				weekNumber,
				weekday,
				weekdayBit,
				scheduleGroup);
	}

	public LocalDateTime getRepetitionStartDate() {
		return repetitionStartDate;
	}
}
