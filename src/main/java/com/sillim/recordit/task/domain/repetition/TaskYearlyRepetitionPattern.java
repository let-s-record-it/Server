package com.sillim.recordit.task.domain.repetition;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.global.util.DateTimeUtils;
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
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskYearlyRepetitionPattern extends TaskRepetitionPattern {

	@Builder(access = AccessLevel.PRIVATE)
	private TaskYearlyRepetitionPattern(
			final TaskRepetitionType repetitionType,
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final TaskMonthOfYear monthOfYear,
			final TaskDayOfMonth dayOfMonth,
			final WeekNumber weekNumber,
			final Weekday weekday,
			final TaskGroup taskGroup) {
		super(
				repetitionType,
				repetitionPeriod,
				repetitionStartDate,
				repetitionEndDate,
				monthOfYear,
				dayOfMonth,
				weekNumber,
				weekday,
				null,
				taskGroup);
	}

	public static TaskRepetitionPattern createYearlyWithDate(
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final Integer monthOfYear,
			final Integer dayOfMonth,
			final TaskGroup taskGroup) {
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.builder()
						.repetitionType(TaskRepetitionType.YEARLY_WITH_DATE)
						.repetitionPeriod(repetitionPeriod)
						.repetitionStartDate(repetitionStartDate)
						.repetitionEndDate(repetitionEndDate)
						.monthOfYear(new TaskMonthOfYear(monthOfYear))
						.dayOfMonth(TaskDayOfMonth.createYearly(monthOfYear, dayOfMonth))
						.taskGroup(taskGroup)
						.build();
		validateMonthOfYearEqualsStartDateMonth(repetitionStartDate, monthOfYear);
		validateDayOfMonthEqualsStartDate(repetitionStartDate, dayOfMonth);
		return repetitionPattern;
	}

	public static TaskRepetitionPattern createYearlyWithWeekday(
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final Integer monthOfYear,
			final WeekNumber weekNumber,
			final Weekday weekday,
			final TaskGroup taskGroup) {
		validateWeekNumberAndWeekdayIsNotNull(weekNumber, weekday);
		validateWeekNumberEqualsStartDate(repetitionStartDate, weekNumber);
		validateWeekdayEqualsStartDate(repetitionStartDate, weekday);
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.builder()
						.repetitionType(TaskRepetitionType.YEARLY_WITH_WEEKDAY)
						.repetitionPeriod(repetitionPeriod)
						.repetitionStartDate(repetitionStartDate)
						.repetitionEndDate(repetitionEndDate)
						.monthOfYear(new TaskMonthOfYear(monthOfYear))
						.weekNumber(weekNumber)
						.weekday(weekday)
						.taskGroup(taskGroup)
						.build();
		validateMonthOfYearEqualsStartDateMonth(repetitionStartDate, monthOfYear);
		return repetitionPattern;
	}

	public static TaskRepetitionPattern createYearlyWithLastDay(
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final Integer monthOfYear,
			final TaskGroup taskGroup) {
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.builder()
						.repetitionType(TaskRepetitionType.YEARLY_WITH_LAST_DAY)
						.repetitionPeriod(repetitionPeriod)
						.repetitionStartDate(repetitionStartDate)
						.repetitionEndDate(repetitionEndDate)
						.monthOfYear(new TaskMonthOfYear(monthOfYear))
						.taskGroup(taskGroup)
						.build();
		validateMonthOfYearEqualsStartDateMonth(repetitionStartDate, monthOfYear);
		return repetitionPattern;
	}

	private static void validateMonthOfYearEqualsStartDateMonth(
			final LocalDate startDate, final Integer monthOfYear) {
		if (startDate.getMonth().getValue() != monthOfYear) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_TASK_MONTH_OF_YEAR);
		}
	}

	private static void validateDayOfMonthEqualsStartDate(
			final LocalDate startDate, final Integer dayOfMonth) {
		if (startDate.getDayOfMonth() != dayOfMonth) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_TASK_DAY_OF_MONTH);
		}
	}

	private static void validateWeekNumberAndWeekdayIsNotNull(
			WeekNumber weekNumber, Weekday weekday) {
		if (Objects.isNull(weekNumber)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_WEEK_NUMBER);
		}
		if (Objects.isNull(weekday)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_WEEKDAY);
		}
	}

	private static void validateWeekNumberEqualsStartDate(
			final LocalDate startDate, final WeekNumber weekNumber) {
		if (!weekNumber.contains(startDate)) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_TASK_WEEK_NUMBER);
		}
	}

	private static void validateWeekdayEqualsStartDate(
			final LocalDate startDate, final Weekday weekday) {
		if (!weekday.hasSameWeekday(startDate)) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_TASK_WEEKDAY);
		}
	}

	@Override
	public Stream<TemporalAmount> repeatingStream() {
		return switch (getRepetitionType()) {
			case YEARLY_WITH_DATE -> yearlyWithDateRepeatingStream();
			case YEARLY_WITH_WEEKDAY -> yearlyWithWeekdayRepeatingStream();
			case YEARLY_WITH_LAST_DAY -> yearlyWithLastDayRepeatingStream();
			default -> throw new InvalidRepetitionException(ErrorCode.INVALID_REPETITION_TYPE);
		};
	}

	private Stream<TemporalAmount> yearlyWithDateRepeatingStream() {
		return Stream.iterate(
						getRepetitionStartDate(),
						date -> date.isBefore(getRepetitionEndDate().plusDays(1L)),
						date ->
								DateTimeUtils.correctDayOfMonth(
										date.plusYears(getRepetitionPeriod()), getDayOfMonth()))
				.filter(date -> date.getDayOfMonth() == getDayOfMonth())
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														getRepetitionStartDate(), date)));
	}

	private Stream<TemporalAmount> yearlyWithWeekdayRepeatingStream() {
		return Stream.iterate(
						getRepetitionStartDate(),
						date ->
								date.isBefore(
										getRepetitionEndDate()
												.with(TemporalAdjusters.lastDayOfMonth())
												.plusDays(1L)),
						date -> date.plusYears(getRepetitionPeriod()))
				.filter(this::hasWeekNumber)
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														getRepetitionStartDate(),
														findDateByWeekday(date))));
	}

	private Stream<TemporalAmount> yearlyWithLastDayRepeatingStream() {
		return Stream.iterate(
						getRepetitionStartDate(),
						date -> date.isBefore(getRepetitionEndDate().plusDays(1L)),
						date -> date.plusYears(getRepetitionPeriod()))
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														getRepetitionStartDate(),
														date.with(
																TemporalAdjusters
																		.lastDayOfMonth()))));
	}

	private boolean hasWeekNumber(final LocalDate date) {
		LocalDate firstDayOfMonth = date.withDayOfMonth(1);
		LocalDate firstWeekday = firstDayOfMonth.with(DayOfWeek.of(getWeekday()));
		if (firstWeekday.isBefore(firstDayOfMonth)) {
			firstWeekday = firstWeekday.plusWeeks(1);
		}
		LocalDate targetWeekday = firstWeekday.plusWeeks(getWeekNumber() - 1);
		// targetWeekday가 당월을 넘지 않는다면 true 반환 (n번째 m요일이 존재한다.)
		return targetWeekday.isBefore(date.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1L));
	}

	private LocalDate findDateByWeekday(final LocalDate date) {
		LocalDate firstDayOfMonth = date.withDayOfMonth(1);
		LocalDate firstWeekday = firstDayOfMonth.with(DayOfWeek.of(getWeekday()));
		if (firstWeekday.isBefore(firstDayOfMonth)) {
			firstWeekday = firstWeekday.plusWeeks(1);
		}
		return firstWeekday.plusWeeks(getWeekNumber() - 1);
	}
}
