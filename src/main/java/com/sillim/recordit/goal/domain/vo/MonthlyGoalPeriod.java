package com.sillim.recordit.goal.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidPeriodException;
import java.time.LocalDate;
import java.time.YearMonth;

public class MonthlyGoalPeriod extends GoalPeriod {

	public MonthlyGoalPeriod(LocalDate startDate, LocalDate endDate) {
		super(startDate, endDate);
		checkIfHasSameYearMonth(startDate, endDate);
		checkIfFirstDayOfMonth(startDate);
		checkIfLastDayOfMonth(endDate);
	}

	/* 시작일과 종료일의 년, 월은 같아야 한다. */
	private void checkIfHasSameYearMonth(final LocalDate startDate, final LocalDate endDate) {

		if (!hasSameYearMonth(startDate, endDate)) {
			throw new InvalidPeriodException(ErrorCode.DIFFERENT_YEAR_MONTH);
		}
	}

	private boolean hasSameYearMonth(final LocalDate startDate, final LocalDate endDate) {

		return (startDate.getYear() == endDate.getYear())
				&& (startDate.getMonth() == endDate.getMonth());
	}

	/* 월 목표의 시작일은 1일이어야 한다. */
	private void checkIfFirstDayOfMonth(final LocalDate startDate) {

		if (startDate.getDayOfMonth() != 1) {
			throw new InvalidPeriodException(ErrorCode.INVALID_START_DAY_OF_MONTH);
		}
	}

	// * 월 목표의 종료일은 해당 월의 말일이어야 한다. */
	private void checkIfLastDayOfMonth(final LocalDate endDate) {

		int lastDayOfMonth = YearMonth.from(endDate).lengthOfMonth();
		if (endDate.getDayOfMonth() != lastDayOfMonth) {
			throw new InvalidPeriodException(
					ErrorCode.INVALID_END_DAY_OF_MONTH,
					ErrorCode.INVALID_END_DAY_OF_MONTH.getFormattedDescription(lastDayOfMonth));
		}
	}
}
