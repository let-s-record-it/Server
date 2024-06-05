package com.sillim.recordit.task.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidWeekdayBitException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.DayOfWeek;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskWeekdayBit {

	private static final int MIN_WEEKDAY_BIT = 0;
	private static final int MAX_WEEKDAY_BIT = 127;

	@Column private Integer weekdayBit;

	public TaskWeekdayBit(Integer weekdayBit) {
		validate(weekdayBit);
		this.weekdayBit = weekdayBit;
	}

	private void validate(Integer weekdayBit) {
		if (weekdayBit < MIN_WEEKDAY_BIT || weekdayBit > MAX_WEEKDAY_BIT) {
			throw new InvalidWeekdayBitException(ErrorCode.WEEKDAY_BIT_OUT_OF_RANGE);
		}
	}

	public boolean isValidWeekday(DayOfWeek dayOfWeek) {
		return (weekdayBit & (1 << (dayOfWeek.getValue() - 1))) > 0;
	}
}
