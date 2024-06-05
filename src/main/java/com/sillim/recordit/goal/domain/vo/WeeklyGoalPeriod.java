package com.sillim.recordit.goal.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidPeriodException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class WeeklyGoalPeriod {

	@Column(nullable = false)
	private final LocalDate startDate;

	@Column(nullable = false)
	private final LocalDate endDate;

	public WeeklyGoalPeriod(final LocalDate startDate, final LocalDate endDate) {
		validateIsNotNull(startDate, endDate);
		validateIsSunday(startDate);
		validateDifferenceOfDate(startDate, endDate);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	private void validateIsNotNull(final LocalDate startDate, final LocalDate endDate) {

		if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
			throw new InvalidPeriodException(ErrorCode.NULL_GOAL_PERIOD);
		}
	}

	/* 주 목표의 시작일은 일요일이어야 한다. */
	private void validateIsSunday(final LocalDate startDate) {

		if (startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
			throw new InvalidPeriodException(ErrorCode.INVALID_START_DAY_OF_WEEK);
		}
	}

	// * 주 목표의 시작일과 종료일은 6일 차이가 나야 한다. */
	private void validateDifferenceOfDate(final LocalDate startDate, final LocalDate endDate) {

		if (ChronoUnit.DAYS.between(startDate, endDate) != 6) {
			throw new InvalidPeriodException(ErrorCode.INVALID_DIFFERENCE_OF_DATE);
		}
	}
}
