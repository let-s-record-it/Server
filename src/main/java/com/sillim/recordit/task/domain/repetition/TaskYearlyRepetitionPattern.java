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
import com.sillim.recordit.task.domain.vo.TaskWeekdayBit;
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

	public static TaskRepetitionPattern createYearlyWithWeekday(
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final Integer monthOfYear,
			final WeekNumber weekNumber,
			final Weekday weekday,
			final TaskGroup taskGroup) {
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

	public static TaskRepetitionPattern createYearlyWithLastDay(
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final Integer monthOfYear,
			final TaskGroup taskGroup) {
		return TaskYearlyRepetitionPattern.builder()
				.repetitionType(TaskRepetitionType.YEARLY_WITH_LAST_DAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.monthOfYear(new TaskMonthOfYear(monthOfYear))
				.taskGroup(taskGroup)
				.build();
	}

	@Override
	protected void validate(
			final TaskRepetitionType repetitionType,
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final TaskMonthOfYear monthOfYear,
			final TaskDayOfMonth dayOfMonth,
			final WeekNumber weekNumber,
			final Weekday weekday,
			final TaskWeekdayBit weekdayBit) {
		super.validate(
				repetitionType,
				repetitionPeriod,
				repetitionStartDate,
				repetitionEndDate,
				monthOfYear,
				dayOfMonth,
				weekNumber,
				weekday,
				weekdayBit);
		switch (repetitionType) {
			case YEARLY_WITH_DATE -> {
				validateMonthOfYearEqualsStartDateMonth(
						repetitionStartDate, monthOfYear.getMonthOfYear());
				validateDayOfMonthEqualsStartDate(repetitionStartDate, dayOfMonth.getDayOfMonth());
			}
			case YEARLY_WITH_WEEKDAY -> {
				validateMonthOfYearEqualsStartDateMonth(
						repetitionStartDate, monthOfYear.getMonthOfYear());
				validateWeekNumberEqualsStartDate(repetitionStartDate, weekNumber);
				validateWeekdayEqualsStartDate(repetitionStartDate, weekday);
			}
			case YEARLY_WITH_LAST_DAY ->
					validateMonthOfYearEqualsStartDateMonth(
							repetitionStartDate, monthOfYear.getMonthOfYear());
		}
	}

	private void validateMonthOfYearEqualsStartDateMonth(
			final LocalDate startDate, final Integer monthOfYear) {
		if (startDate.getMonth().getValue() != monthOfYear) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_TASK_MONTH_OF_YEAR);
		}
	}

	private void validateDayOfMonthEqualsStartDate(
			final LocalDate startDate, final Integer dayOfMonth) {
		if (startDate.getDayOfMonth() != dayOfMonth) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_TASK_DAY_OF_MONTH);
		}
	}

	private void validateWeekNumberEqualsStartDate(
			final LocalDate startDate, final WeekNumber weekNumber) {
		if (Objects.isNull(weekNumber)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_WEEK_NUMBER);
		}
		if (!weekNumber.contains(startDate)) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_TASK_WEEK_NUMBER);
		}
	}

	private void validateWeekdayEqualsStartDate(final LocalDate startDate, final Weekday weekday) {
		if (Objects.isNull(weekday)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_WEEKDAY);
		}
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
										date.plusYears(getRepetitionPeriod()), dayOfMonth()))
				.filter(date -> date.getDayOfMonth() == dayOfMonth())
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

	/**
	 * @return targetWeekday가 당월을 넘지 않는다면 true 반환 (n번째 m요일이 존재한다.)
	 */
	private boolean hasWeekNumber(final LocalDate date) {

		LocalDate firstDayOfMonth = date.withDayOfMonth(1);
		LocalDate firstWeekday = firstDayOfMonth.with(DayOfWeek.of(weekday()));

		if (firstWeekday.isBefore(firstDayOfMonth)) {
			firstWeekday = firstWeekday.plusWeeks(1);
		}
		LocalDate targetWeekday = firstWeekday.plusWeeks(weekNumber() - 1);

		return targetWeekday.isBefore(date.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1L));
	}

	private LocalDate findDateByWeekday(final LocalDate date) {
		LocalDate firstDayOfMonth = date.withDayOfMonth(1);
		LocalDate firstWeekday = firstDayOfMonth.with(DayOfWeek.of(weekday()));
		if (firstWeekday.isBefore(firstDayOfMonth)) {
			firstWeekday = firstWeekday.plusWeeks(1);
		}
		return firstWeekday.plusWeeks(weekNumber() - 1);
	}

	private Integer dayOfMonth() {
		return getDayOfMonth()
				.orElseThrow(
						() -> new InvalidRepetitionException(ErrorCode.NULL_TASK_DAY_OF_MONTH));
	}

	private Integer weekday() {
		return getWeekday()
				.map(Weekday::getValue)
				.orElseThrow(
						() ->
								new InvalidRepetitionException(
										ErrorCode.NULL_TASK_REPETITION_WEEKDAY));
	}

	private Integer weekNumber() {
		return getWeekNumber()
				.map(WeekNumber::getValue)
				.orElseThrow(
						() ->
								new InvalidRepetitionException(
										ErrorCode.NULL_TASK_REPETITION_WEEKDAY));
	}
}
