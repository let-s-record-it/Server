package com.sillim.recordit.task.domain.repetition;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.schedule.domain.WeekNumber;
import com.sillim.recordit.schedule.domain.Weekday;
import com.sillim.recordit.schedule.domain.vo.DayOfMonth;
import com.sillim.recordit.task.domain.TaskGroup;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;

class TaskMonthlyRepetitionPattern extends TaskRepetitionPattern {

	@Builder(access = AccessLevel.PRIVATE)
	private TaskMonthlyRepetitionPattern(
			RepetitionType repetitionType,
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			DayOfMonth dayOfMonth,
			WeekNumber weekNumber,
			Weekday weekday,
			TaskGroup taskGroup) {
		this.repetitionType = repetitionType;
		this.repetitionPeriod = repetitionPeriod;
		this.repetitionStartDate = repetitionStartDate;
		this.repetitionEndDate = repetitionEndDate;
		this.dayOfMonth = dayOfMonth;
		this.weekNumber = weekNumber;
		this.weekday = weekday;
		this.taskGroup = taskGroup;
	}

	public static TaskMonthlyRepetitionPattern createMonthlyWithDate(
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			Integer dayOfMonth,
			TaskGroup taskGroup) {
		validateDayOfMonthEqualsStartDate(repetitionStartDate, dayOfMonth);
		return TaskMonthlyRepetitionPattern.builder()
				.repetitionType(RepetitionType.MONTHLY_WITH_DATE)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.dayOfMonth(DayOfMonth.createMonthly(dayOfMonth))
				.taskGroup(taskGroup)
				.build();
	}

	public static TaskMonthlyRepetitionPattern createMonthlyWithWeekday(
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			WeekNumber weekNumber,
			Weekday weekday,
			TaskGroup taskGroup) {
		return TaskMonthlyRepetitionPattern.builder()
				.repetitionType(RepetitionType.MONTHLY_WITH_WEEKDAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.weekNumber(weekNumber)
				.weekday(weekday)
				.taskGroup(taskGroup)
				.build();
	}

	public static TaskMonthlyRepetitionPattern createMonthlyWithLastDay(
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			TaskGroup taskGroup) {
		return TaskMonthlyRepetitionPattern.builder()
				.repetitionType(RepetitionType.MONTHLY_WITH_LAST_DAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.taskGroup(taskGroup)
				.build();
	}

	private static void validateDayOfMonthEqualsStartDate(LocalDate startDate, Integer dayOfMonth) {
		if (startDate.getDayOfMonth() != dayOfMonth) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_DAY_OF_MONTH);
		}
	}

	@Override
	public Stream<TemporalAmount> repeatingStream() {
		return switch (repetitionType) {
			case MONTHLY_WITH_DATE -> monthlyWithDateRepeatingStream();
			case MONTHLY_WITH_WEEKDAY -> monthlyWithWeekdayRepeatingStream();
			case MONTHLY_WITH_LAST_DAY -> monthlyWithLastDayRepeatingStream();
			default -> throw new InvalidRepetitionException(ErrorCode.INVALID_REPETITION_TYPE);
		};
	}

	private Stream<TemporalAmount> monthlyWithDateRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date -> date.isBefore(repetitionEndDate.plusDays(1L)),
						date -> date.plusMonths(repetitionPeriod))
				.map(
						date ->
								Period.ofDays(
										(int) ChronoUnit.DAYS.between(repetitionStartDate, date)));
	}

	private Stream<TemporalAmount> monthlyWithWeekdayRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date ->
								date.isBefore(
										repetitionEndDate
												.with(TemporalAdjusters.lastDayOfMonth())
												.plusDays(1L)),
						date -> date.plusMonths(repetitionPeriod))
				.filter(date -> findDateByWeekday(date).isBefore(repetitionEndDate))
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														repetitionStartDate,
														findDateByWeekday(date))));
	}

	private Stream<TemporalAmount> monthlyWithLastDayRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date -> date.isBefore(repetitionEndDate.plusDays(1L)),
						date -> date.plusMonths(repetitionPeriod))
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														repetitionStartDate,
														date.with(
																TemporalAdjusters
																		.lastDayOfMonth()))));
	}

	private LocalDate findDateByWeekday(LocalDate date) {
		LocalDate firstDayOfMonth = date.withDayOfMonth(1);
		DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();

		int daysUntilWeekday = weekday.getValue() - firstDayOfWeek.getValue();
		if (daysUntilWeekday < 0) {
			daysUntilWeekday += 7;
		}
		return firstDayOfMonth.plusDays(daysUntilWeekday + ((weekNumber.getValue() - 1) * 7L));
	}
}
