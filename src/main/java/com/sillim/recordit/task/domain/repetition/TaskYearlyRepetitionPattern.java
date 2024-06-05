package com.sillim.recordit.task.domain.repetition;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.vo.TaskDayOfMonth;
import com.sillim.recordit.task.domain.vo.TaskMonthOfYear;
import jakarta.persistence.Entity;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TaskYearlyRepetitionPattern extends TaskRepetitionPattern {

	@Builder(access = AccessLevel.PRIVATE)
	private TaskYearlyRepetitionPattern(
			TaskRepetitionType repetitionType,
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			TaskMonthOfYear monthOfYear,
			TaskDayOfMonth dayOfMonth,
			WeekNumber weekNumber,
			Weekday weekday,
			TaskGroup taskGroup) {
		this.repetitionType = repetitionType;
		this.repetitionPeriod = repetitionPeriod;
		this.repetitionStartDate = repetitionStartDate;
		this.repetitionEndDate = repetitionEndDate;
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		this.weekNumber = weekNumber;
		this.weekday = weekday;
		this.taskGroup = taskGroup;
	}

	public static TaskYearlyRepetitionPattern createYearlyWithDate(
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			Integer monthOfYear,
			Integer dayOfMonth,
			TaskGroup taskGroup) {
		validateMonthOfYearEqualsStartDateMonth(repetitionStartDate, monthOfYear);
		validateDayOfMonthEqualsStartDate(repetitionStartDate, dayOfMonth);
		return TaskYearlyRepetitionPattern.builder()
				.repetitionType(TaskRepetitionType.YEARLY_WITH_DATE)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.monthOfYear(new TaskMonthOfYear(monthOfYear))
				.dayOfMonth(TaskDayOfMonth.createYearly(monthOfYear, dayOfMonth))
				.taskGroup(taskGroup)
				.build();
	}

	public static TaskYearlyRepetitionPattern createYearlyWithWeekday(
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			Integer monthOfYear,
			WeekNumber weekNumber,
			Weekday weekday,
			TaskGroup taskGroup) {
		validateMonthOfYearEqualsStartDateMonth(repetitionStartDate, monthOfYear);
		return TaskYearlyRepetitionPattern.builder()
				.repetitionType(TaskRepetitionType.YEARLY_WITH_WEEKDAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.monthOfYear(new TaskMonthOfYear(monthOfYear))
				.weekNumber(weekNumber)
				.weekday(weekday)
				.taskGroup(taskGroup)
				.build();
	}

	public static TaskYearlyRepetitionPattern createYearlyWithLastDay(
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			Integer monthOfYear,
			TaskGroup taskGroup) {
		validateMonthOfYearEqualsStartDateMonth(repetitionStartDate, monthOfYear);
		return TaskYearlyRepetitionPattern.builder()
				.repetitionType(TaskRepetitionType.YEARLY_WITH_LAST_DAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.monthOfYear(new TaskMonthOfYear(monthOfYear))
				.taskGroup(taskGroup)
				.build();
	}

	private static void validateDayOfMonthEqualsStartDate(LocalDate startDate, Integer dayOfMonth) {
		if (startDate.getDayOfMonth() != dayOfMonth) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_DAY_OF_MONTH);
		}
	}

	private static void validateMonthOfYearEqualsStartDateMonth(
			LocalDate startDate, Integer monthOfYear) {
		if (startDate.getMonth().getValue() != monthOfYear) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_MONTH_OF_YEAR);
		}
	}

	@Override
	public Stream<TemporalAmount> repeatingStream() {
		return switch (repetitionType) {
			case YEARLY_WITH_DATE -> yearlyWithDateRepeatingStream();
			case YEARLY_WITH_WEEKDAY -> yearlyWithWeekdayRepeatingStream();
			case YEARLY_WITH_LAST_DAY -> yearlyWithLastDayRepeatingStream();
			default -> throw new InvalidRepetitionException(ErrorCode.INVALID_REPETITION_TYPE);
		};
	}

	private Stream<TemporalAmount> yearlyWithDateRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date -> date.isBefore(repetitionEndDate.plusDays(1L)),
						date -> date.plusYears(repetitionPeriod))
				.filter(date -> dayOfMonth.equalsDayOfMonthValue(date.getDayOfMonth()))
				.map(
						date ->
								Period.ofDays(
										(int) ChronoUnit.DAYS.between(repetitionStartDate, date)));
	}

	private Stream<TemporalAmount> yearlyWithWeekdayRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date ->
								date.isBefore(
										repetitionEndDate
												.with(TemporalAdjusters.lastDayOfMonth())
												.plusDays(1L)),
						date -> date.plusYears(repetitionPeriod))
				.filter(date -> findDateByWeekday(date).isBefore(repetitionEndDate))
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														repetitionStartDate,
														findDateByWeekday(date))));
	}

	private Stream<TemporalAmount> yearlyWithLastDayRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date -> date.isBefore(repetitionEndDate.plusDays(1L)),
						date -> date.plusYears(repetitionPeriod))
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
