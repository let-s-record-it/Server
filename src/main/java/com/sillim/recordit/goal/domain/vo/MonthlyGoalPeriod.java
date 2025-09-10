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
public class MonthlyGoalPeriod {

	@Column(nullable = false)
	private final LocalDate startDate;

	@Column(nullable = false)
	private final LocalDate endDate;

	public MonthlyGoalPeriod(final LocalDate startDate, final LocalDate endDate) {
		validateIsNotNull(startDate, endDate);
		validateHasSameYearMonth(startDate, endDate);
		validateIsFirstDayOfMonth(startDate);
		validateIsLastDayOfMonth(endDate);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	private void validateIsNotNull(final LocalDate startDate, final LocalDate endDate) {

		if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
			throw new InvalidPeriodException(ErrorCode.NULL_GOAL_PERIOD);
		}
	}

	/* 시작일과 종료일의 년, 월은 같아야 한다. */
	private void validateHasSameYearMonth(final LocalDate startDate, final LocalDate endDate) {

		if (!hasSameYearMonth(startDate, endDate)) {
			throw new InvalidPeriodException(ErrorCode.DIFFERENT_YEAR_MONTH);
		}
	}

	private boolean hasSameYearMonth(final LocalDate startDate, final LocalDate endDate) {

		return (startDate.getYear() == endDate.getYear())
				&& (startDate.getMonth() == endDate.getMonth());
	}

	/* 월 목표의 시작일은 1일이어야 한다. */
	private void validateIsFirstDayOfMonth(final LocalDate startDate) {

		if (startDate.getDayOfMonth() != 1) {
			throw new InvalidPeriodException(ErrorCode.INVALID_START_DAY_OF_MONTH);
		}
	}

	// * 월 목표의 종료일은 해당 월의 말일이어야 한다. */
	private void validateIsLastDayOfMonth(final LocalDate endDate) {

		int lastDayOfMonth = YearMonth.from(endDate).lengthOfMonth();
		if (endDate.getDayOfMonth() != lastDayOfMonth) {
			throw new InvalidPeriodException(
					ErrorCode.INVALID_END_DAY_OF_MONTH,
					ErrorCode.INVALID_END_DAY_OF_MONTH.getFormattedDescription(lastDayOfMonth));
		}
	}
}
