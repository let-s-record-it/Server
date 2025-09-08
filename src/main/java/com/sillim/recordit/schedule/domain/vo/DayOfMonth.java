package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidDayOfMonthException;
import com.sillim.recordit.schedule.domain.RepetitionType;
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
public class DayOfMonth {

	@Column
	private Integer dayOfMonth;

	private DayOfMonth(Integer dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public static DayOfMonth createMonthly(Integer dayOfMonth) {
		validateDateRange(dayOfMonth);
		return new DayOfMonth(dayOfMonth);
	}

	public static DayOfMonth createYearly(Integer monthOfYear, Integer dayOfMonth) {
		validateDateOfMonth(monthOfYear, dayOfMonth);
		return new DayOfMonth(dayOfMonth);
	}

	public static DayOfMonth create(RepetitionType repetitionType, Integer monthOfYear, Integer dayOfMonth) {
		return switch (repetitionType) {
			case MONTHLY_WITH_DATE, MONTHLY_WITH_WEEKDAY, MONTHLY_WITH_LAST_DAY -> createMonthly(dayOfMonth);
			case YEARLY_WITH_DATE, YEARLY_WITH_WEEKDAY, YEARLY_WITH_LAST_DAY -> createYearly(monthOfYear, dayOfMonth);
			default -> null;
		};
	}

	public boolean equalsDayOfMonthValue(Integer dayOfMonth) {
		return this.dayOfMonth.equals(dayOfMonth);
	}

	private static void validateDateOfMonth(Integer monthOfYear, Integer dayOfMonth) {
		validateDateRange(dayOfMonth);

		if (monthOfYear == 2 && dayOfMonth > 29) {
			throw new InvalidDayOfMonthException(ErrorCode.INVALID_DAY_OF_MONTH);
		}

		if ((monthOfYear == 4 || monthOfYear == 6 || monthOfYear == 9 || monthOfYear == 11) && dayOfMonth > 30) {
			throw new InvalidDayOfMonthException(ErrorCode.INVALID_DAY_OF_MONTH);
		}
	}

	private static void validateDateRange(Integer date) {
		if (date < 1 || date > 31) {
			throw new InvalidDayOfMonthException(ErrorCode.INVALID_DAY_OF_MONTH);
		}
	}
}
