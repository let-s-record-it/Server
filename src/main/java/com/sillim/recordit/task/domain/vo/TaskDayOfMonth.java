package com.sillim.recordit.task.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidDayOfMonthException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskDayOfMonth {

	@Column private Integer dayOfMonth;

	private TaskDayOfMonth(Integer dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public static TaskDayOfMonth createMonthly(Integer dayOfMonth) {
		validateDateRange(dayOfMonth);
		return new TaskDayOfMonth(dayOfMonth);
	}

	public static TaskDayOfMonth createYearly(Integer monthOfYear, Integer dayOfMonth) {
		validateDateOfMonth(monthOfYear, dayOfMonth);
		return new TaskDayOfMonth(dayOfMonth);
	}

	public boolean equalsDayOfMonthValue(Integer dayOfMonth) {
		return this.dayOfMonth.equals(dayOfMonth);
	}

	private static void validateDateOfMonth(Integer monthOfYear, Integer dayOfMonth) {
		validateDateRange(dayOfMonth);

		if (monthOfYear == 2 && dayOfMonth > 29) {
			throw new InvalidDayOfMonthException(ErrorCode.INVALID_DAY_OF_MONTH);
		}

		if ((monthOfYear == 4 || monthOfYear == 6 || monthOfYear == 9 || monthOfYear == 11)
				&& dayOfMonth > 30) {
			throw new InvalidDayOfMonthException(ErrorCode.INVALID_DAY_OF_MONTH);
		}
	}

	private static void validateDateRange(Integer date) {
		if (date < 1 || date > 31) {
			throw new InvalidDayOfMonthException(ErrorCode.INVALID_DAY_OF_MONTH);
		}
	}
}
