package com.sillim.recordit.task.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidDayOfMonthException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public final class TaskDayOfMonth {

	@Column private final Integer dayOfMonth;

	private TaskDayOfMonth(final Integer dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public static TaskDayOfMonth createMonthly(final Integer dayOfMonth) {
		if (Objects.isNull(dayOfMonth)) {
			throw new InvalidDayOfMonthException(ErrorCode.NULL_TASK_DAY_OF_MONTH);
		}
		validateDateRange(dayOfMonth);
		return new TaskDayOfMonth(dayOfMonth);
	}

	public static TaskDayOfMonth createYearly(final Integer monthOfYear, final Integer dayOfMonth) {
		if (Objects.isNull(monthOfYear)) {
			throw new InvalidDayOfMonthException(ErrorCode.NULL_TASK_MONTH_OF_YEAR);
		}
		if (Objects.isNull(dayOfMonth)) {
			throw new InvalidDayOfMonthException(ErrorCode.NULL_TASK_DAY_OF_MONTH);
		}
		validateDateRange(dayOfMonth);
		validateDateOfMonth(monthOfYear, dayOfMonth);
		return new TaskDayOfMonth(dayOfMonth);
	}

	private static void validateDateRange(final Integer date) {
		if (date < 1 || date > 31) {
			throw new InvalidDayOfMonthException(ErrorCode.TASK_DAY_OF_MONTH_OUT_OF_RANGE);
		}
	}

	private static void validateDateOfMonth(final Integer monthOfYear, final Integer dayOfMonth) {
		if (isOutOfBounds(monthOfYear, dayOfMonth)) {
			throw new InvalidDayOfMonthException(ErrorCode.TASK_DAY_OF_MONTH_OUT_OF_RANGE);
		}
	}

	private static boolean isOutOfBounds(final Integer monthOfYear, final Integer dayOfMonth) {

		return switch (monthOfYear) {
			case 2 -> dayOfMonth > 29;
			case 4, 6, 9, 11 -> dayOfMonth > 30;
			default -> false;
		};
	}
}
