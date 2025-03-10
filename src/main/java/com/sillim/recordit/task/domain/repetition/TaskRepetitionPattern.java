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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted = false")
public abstract class TaskRepetitionPattern extends BaseTime {

	private static final int MAX_PERIOD = 999;
	private static final int MIN_PERIOD = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_repetition_pattern_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TaskRepetitionType repetitionType;

	@Column(nullable = false)
	private Integer repetitionPeriod;

	@Column(nullable = false)
	private LocalDate repetitionStartDate;

	@Column(nullable = false)
	private LocalDate repetitionEndDate;

	@Embedded private TaskMonthOfYear monthOfYear;

	@Embedded private TaskDayOfMonth dayOfMonth;

	@Column private WeekNumber weekNumber;

	@Column private Weekday weekday;

	@Embedded private TaskWeekdayBit weekdayBit;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean deleted = false;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_group_id")
	private TaskGroup taskGroup;

	protected TaskRepetitionPattern(
			final TaskRepetitionType repetitionType,
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final TaskMonthOfYear monthOfYear,
			final TaskDayOfMonth dayOfMonth,
			final WeekNumber weekNumber,
			final Weekday weekday,
			final TaskWeekdayBit weekdayBit,
			final TaskGroup taskGroup) {
		validate(
				repetitionType,
				repetitionPeriod,
				repetitionStartDate,
				repetitionEndDate,
				monthOfYear,
				dayOfMonth,
				weekNumber,
				weekday,
				weekdayBit);
		this.repetitionType = repetitionType;
		this.repetitionPeriod = repetitionPeriod;
		this.repetitionStartDate = repetitionStartDate;
		this.repetitionEndDate = repetitionEndDate;
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		this.weekNumber = weekNumber;
		this.weekday = weekday;
		this.weekdayBit = weekdayBit;
		this.taskGroup = taskGroup;
	}

	public Optional<Integer> getMonthOfYear() {
		if (monthOfYear == null) {
			return Optional.empty();
		}
		return Optional.of(monthOfYear.getMonthOfYear());
	}

	public Optional<Integer> getDayOfMonth() {
		if (dayOfMonth == null) {
			return Optional.empty();
		}
		return Optional.of(dayOfMonth.getDayOfMonth());
	}

	public Optional<WeekNumber> getWeekNumber() {
		return Optional.ofNullable(weekNumber);
	}

	public Optional<Weekday> getWeekday() {
		return Optional.ofNullable(weekday);
	}

	public Optional<Integer> getWeekdayBit() {
		if (weekdayBit == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(weekdayBit.getWeekdayBit());
	}

	public boolean isValidWeekday(final DayOfWeek dayOfWeek) {
		return (weekdayBit.getWeekdayBit() & (1 << (dayOfWeek.getValue() - 1))) > 0;
	}

	public void setTaskGroup(TaskGroup taskGroup) {
		this.taskGroup = taskGroup;
	}

	public void remove() {
		this.deleted = true;
		this.taskGroup = null;
	}

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
		validatePeriod(repetitionPeriod);
		validateDuration(repetitionStartDate, repetitionEndDate);
	}

	private void validatePeriod(final Integer repetitionPeriod) {
		if (Objects.isNull(repetitionPeriod)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_PERIOD);
		}
		if (repetitionPeriod < MIN_PERIOD || repetitionPeriod > MAX_PERIOD) {
			throw new InvalidRepetitionException(ErrorCode.TASK_REPETITION_PERIOD_OUT_OF_RANGE);
		}
	}

	private void validateDuration(
			final LocalDate repetitionStartDate, final LocalDate repetitionEndDate) {
		if (Objects.isNull(repetitionStartDate) || Objects.isNull(repetitionEndDate)) {
			throw new InvalidRepetitionException(ErrorCode.NULL_TASK_REPETITION_DURATION);
		}
		if (repetitionStartDate.isAfter(repetitionEndDate)) {
			throw new InvalidRepetitionException(ErrorCode.TASK_REPETITION_INVALID_DURATION);
		}
	}

	public abstract Stream<TemporalAmount> repeatingStream();
}
