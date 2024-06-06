package com.sillim.recordit.task.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidMonthOfYearException;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public final class TaskMonthOfYear {

	private static final int MIN_MONTH_OF_YEAR = 1;
	private static final int MAX_MONTH_OF_YEAR = 12;

	private final Integer monthOfYear;

	public TaskMonthOfYear(final Integer monthOfYear) {
		validate(monthOfYear);
		this.monthOfYear = monthOfYear;
	}

	private void validate(final Integer monthOfYear) {
		if (Objects.isNull(monthOfYear)) {
			throw new InvalidMonthOfYearException(ErrorCode.NULL_TASK_MONTH_OF_YEAR);
		}
		if (monthOfYear < MIN_MONTH_OF_YEAR || monthOfYear > MAX_MONTH_OF_YEAR) {
			throw new InvalidMonthOfYearException(ErrorCode.TASK_MONTH_OF_YEAR_OUT_OF_RANGE);
		}
	}
}
