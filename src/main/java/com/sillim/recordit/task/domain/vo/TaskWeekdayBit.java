package com.sillim.recordit.task.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidWeekdayBitException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.DayOfWeek;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public final class TaskWeekdayBit {

	private static final int MIN_WEEKDAY_BIT = 0;
	private static final int MAX_WEEKDAY_BIT = 127;

	@Column private final Integer weekdayBit;

	public TaskWeekdayBit(final Integer weekdayBit) {
		validate(weekdayBit);
		this.weekdayBit = weekdayBit;
	}

	private void validate(final Integer weekdayBit) {
		if (Objects.isNull(weekdayBit)) {
			throw new InvalidWeekdayBitException(ErrorCode.NULL_TASK_WEEKDAY_BIT);
		}
		if (weekdayBit < MIN_WEEKDAY_BIT || weekdayBit > MAX_WEEKDAY_BIT) {
			throw new InvalidWeekdayBitException(ErrorCode.TASK_WEEKDAY_BIT_OUT_OF_RANGE);
		}
	}

	public boolean isValidWeekday(final DayOfWeek dayOfWeek) {
		return (weekdayBit & (1 << (dayOfWeek.getValue() - 1))) > 0;
	}
}
