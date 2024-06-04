package com.sillim.recordit.goal.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidPeriodException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class WeeklyGoalPeriod extends GoalPeriod {
	public WeeklyGoalPeriod(LocalDate startDate, LocalDate endDate) {
		super(startDate, endDate);
		checkIsSunday(startDate);
		checkDifferenceOfDate(startDate, endDate);
	}

	/* 주 목표의 시작일은 일요일이어야 한다. */
	private void checkIsSunday(final LocalDate startDate) {

		if (startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
			throw new InvalidPeriodException(ErrorCode.INVALID_START_DAY_OF_WEEK);
		}
	}

	// * 주 목표의 시작일과 종료일은 6일 차이가 나야 한다. */
	private void checkDifferenceOfDate(final LocalDate startDate, final LocalDate endDate) {

		if (ChronoUnit.DAYS.between(startDate, endDate) != 6) {
			throw new InvalidPeriodException(ErrorCode.INVALID_DIFFERENCE_OF_DATE);
		}
	}
}
