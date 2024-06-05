package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidMonthOfYearException;
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
public class MonthOfYear {

	private static final int MIN_MONTH_OF_YEAR = 1;
	private static final int MAX_MONTH_OF_YEAR = 12;

	private Integer monthOfYear;

	public MonthOfYear(Integer monthOfYear) {
		validate(monthOfYear);
		this.monthOfYear = monthOfYear;
	}

	private void validate(Integer monthOfYear) {
		if (monthOfYear < MIN_MONTH_OF_YEAR || monthOfYear > MAX_MONTH_OF_YEAR) {
			throw new InvalidMonthOfYearException(ErrorCode.INVALID_MONTH_OF_YEAR);
		}
	}
}
