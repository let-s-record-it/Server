package com.sillim.recordit.goal.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidPeriodException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class GoalPeriod {

	@Column(nullable = false)
	private final LocalDate startDate;

	@Column(nullable = false)
	private final LocalDate endDate;

	public GoalPeriod(final LocalDate startDate, final LocalDate endDate) {
		validate(startDate, endDate);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	private static void validate(final LocalDate startDate, final LocalDate endDate) {

		if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
			throw new InvalidPeriodException(ErrorCode.NULL_GOAL_PERIOD);
		}
		checkIfHasSameYearMonth(startDate, endDate);
		checkIfFirstDayOfMonth(startDate);
		checkIfLastDayOfMonth(endDate);
	}

	/* 시작일과 종료일의 년, 월은 같아야 한다. */
	private static void checkIfHasSameYearMonth(
			final LocalDate startDate, final LocalDate endDate) {

		if (!hasSameYearMonth(startDate, endDate)) {
			throw new InvalidPeriodException(ErrorCode.DIFFERENT_YEAR_MONTH);
		}
	}

	private static boolean hasSameYearMonth(final LocalDate startDate, final LocalDate endDate) {

		return (startDate.getYear() == endDate.getYear())
				&& (startDate.getMonth() == endDate.getMonth());
	}

	/* 월 목표의 시작일은 1일이어야 한다. */
	private static void checkIfFirstDayOfMonth(final LocalDate startDate) {

		if (startDate.getDayOfMonth() != 1) {
			throw new InvalidPeriodException(ErrorCode.INVALID_START_DAY_OF_MONTH);
		}
	}

	// * 월 목표의 종료일은 해당 월의 말일이어야 한다. */
	private static void checkIfLastDayOfMonth(final LocalDate endDate) {

		int lastDayOfMonth = YearMonth.from(endDate).lengthOfMonth();
		if (endDate.getDayOfMonth() != lastDayOfMonth) {
			throw new InvalidPeriodException(
					ErrorCode.INVALID_END_DAY_OF_MONTH,
					ErrorCode.INVALID_END_DAY_OF_MONTH.getFormattedDescription(lastDayOfMonth));
		}
	}
}
