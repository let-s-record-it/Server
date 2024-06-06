package com.sillim.recordit.task.domain.repetition;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.vo.TaskDayOfMonth;
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
class TaskMonthlyRepetitionPattern extends TaskRepetitionPattern {

	@Builder(access = AccessLevel.PRIVATE)
	private TaskMonthlyRepetitionPattern(
			final TaskRepetitionType repetitionType,
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final TaskDayOfMonth dayOfMonth,
			final WeekNumber weekNumber,
			final Weekday weekday,
			final TaskGroup taskGroup) {
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
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final Integer dayOfMonth,
			final TaskGroup taskGroup) {
		validateDayOfMonthEqualsStartDate(repetitionStartDate, dayOfMonth);
		return TaskMonthlyRepetitionPattern.builder()
				.repetitionType(TaskRepetitionType.MONTHLY_WITH_DATE)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.dayOfMonth(TaskDayOfMonth.createMonthly(dayOfMonth))
				.taskGroup(taskGroup)
				.build();
	}

	public static TaskMonthlyRepetitionPattern createMonthlyWithWeekday(
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final WeekNumber weekNumber,
			final Weekday weekday,
			final TaskGroup taskGroup) {
		if (Objects.isNull(weekNumber)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_WEEK_NUMBER);
		}
		if (Objects.isNull(weekday)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_WEEKDAY);
		}
		return TaskMonthlyRepetitionPattern.builder()
				.repetitionType(TaskRepetitionType.MONTHLY_WITH_WEEKDAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.weekNumber(weekNumber)
				.weekday(weekday)
				.taskGroup(taskGroup)
				.build();
	}

	public static TaskMonthlyRepetitionPattern createMonthlyWithLastDay(
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final TaskGroup taskGroup) {
		return TaskMonthlyRepetitionPattern.builder()
				.repetitionType(TaskRepetitionType.MONTHLY_WITH_LAST_DAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.taskGroup(taskGroup)
				.build();
	}

	private static void validateDayOfMonthEqualsStartDate(
			final LocalDate startDate, final Integer dayOfMonth) {
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

	private LocalDate findDateByWeekday(final LocalDate date) {
		final LocalDate firstDayOfMonth = date.withDayOfMonth(1);
		final DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();

		int daysUntilWeekday = weekday.getValue() - firstDayOfWeek.getValue();
		if (daysUntilWeekday < 0) {
			daysUntilWeekday += 7;
		}
		return firstDayOfMonth.plusDays(daysUntilWeekday + ((weekNumber.getValue() - 1) * 7L));
	}
}
