package com.sillim.recordit.goal.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidPeriodException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public abstract class GoalPeriod {

	@Column(nullable = false)
	private final LocalDate startDate;

	@Column(nullable = false)
	private final LocalDate endDate;

	public GoalPeriod(final LocalDate startDate, final LocalDate endDate) {
		validate(startDate, endDate);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	private void validate(final LocalDate startDate, final LocalDate endDate) {

		if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
			throw new InvalidPeriodException(ErrorCode.NULL_GOAL_PERIOD);
		}
	}
}
