package com.sillim.recordit.task.domain.repetition;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.vo.TaskDayOfMonth;
import com.sillim.recordit.task.domain.vo.TaskMonthOfYear;
import com.sillim.recordit.task.domain.vo.TaskWeekdayBit;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SQLRestriction("deleted = false")
public abstract class TaskRepetitionPattern extends BaseTime {

	private static final int MAX_PERIOD = 999;
	private static final int MIN_PERIOD = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_repetition_pattern_id", nullable = false)
	protected Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	protected TaskRepetitionType repetitionType;

	@Column(nullable = false)
	protected Integer repetitionPeriod;

	@Column(nullable = false)
	protected LocalDate repetitionStartDate;

	@Column(nullable = false)
	protected LocalDate repetitionEndDate;

	@Embedded protected TaskMonthOfYear monthOfYear;

	@Embedded protected TaskDayOfMonth dayOfMonth;

	@Column protected WeekNumber weekNumber;

	@Column protected Weekday weekday;

	@Embedded protected TaskWeekdayBit weekdayBit;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_group_id", unique = true)
	protected TaskGroup taskGroup;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean deleted = false;

	public static TaskRepetitionPattern create(
			final TaskRepetitionType repetitionType,
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final Integer monthOfYear,
			final Integer dayOfMonth,
			final WeekNumber weekNumber,
			final Weekday weekday,
			final Integer weekdayBit,
			final TaskGroup taskGroup) {
		validateRepetitionType(repetitionType);
		validatePeriod(repetitionPeriod);
		validateDuration(repetitionStartDate, repetitionEndDate);
		return switch (repetitionType) {
			case DAILY ->
					TaskDailyRepetitionPattern.createDaily(
							repetitionPeriod, repetitionStartDate, repetitionEndDate, taskGroup);
			case WEEKLY ->
					TaskWeeklyRepetitionPattern.createWeekly(
							repetitionPeriod,
							repetitionStartDate,
							repetitionEndDate,
							weekdayBit,
							taskGroup);
			case MONTHLY_WITH_DATE ->
					TaskMonthlyRepetitionPattern.createMonthlyWithDate(
							repetitionPeriod,
							repetitionStartDate,
							repetitionEndDate,
							dayOfMonth,
							taskGroup);
			case MONTHLY_WITH_WEEKDAY ->
					TaskMonthlyRepetitionPattern.createMonthlyWithWeekday(
							repetitionPeriod,
							repetitionStartDate,
							repetitionEndDate,
							weekNumber,
							weekday,
							taskGroup);
			case MONTHLY_WITH_LAST_DAY ->
					TaskMonthlyRepetitionPattern.createMonthlyWithLastDay(
							repetitionPeriod, repetitionStartDate, repetitionEndDate, taskGroup);
			case YEARLY_WITH_DATE ->
					TaskYearlyRepetitionPattern.createYearlyWithDate(
							repetitionPeriod,
							repetitionStartDate,
							repetitionEndDate,
							monthOfYear,
							dayOfMonth,
							taskGroup);
			case YEARLY_WITH_WEEKDAY ->
					TaskYearlyRepetitionPattern.createYearlyWithWeekday(
							repetitionPeriod,
							repetitionStartDate,
							repetitionEndDate,
							monthOfYear,
							weekNumber,
							weekday,
							taskGroup);
			case YEARLY_WITH_LAST_DAY ->
					TaskYearlyRepetitionPattern.createYearlyWithLastDay(
							repetitionPeriod,
							repetitionStartDate,
							repetitionEndDate,
							monthOfYear,
							taskGroup);
		};
	}

	private static void validateRepetitionType(final TaskRepetitionType repetitionType) {
		if (Objects.isNull(repetitionType)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_TYPE);
		}
	}

	private static void validatePeriod(final Integer repetitionPeriod) {
		if (Objects.isNull(repetitionPeriod)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_PERIOD);
		}
		if (repetitionPeriod < MIN_PERIOD || repetitionPeriod > MAX_PERIOD) {
			throw new InvalidRepetitionException(ErrorCode.INVALID_REPETITION_PERIOD);
		}
	}

	private static void validateDuration(
			final LocalDate repetitionStartDate, final LocalDate repetitionEndDate) {
		if (Objects.isNull(repetitionStartDate) || Objects.isNull(repetitionEndDate)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_PERIOD);
		}
		if (repetitionStartDate.isAfter(repetitionEndDate)) {
			throw new InvalidRepetitionException(ErrorCode.TASK_REPETITION_INVALID_DURATION);
		}
	}

	public abstract Stream<TemporalAmount> repeatingStream();
}
