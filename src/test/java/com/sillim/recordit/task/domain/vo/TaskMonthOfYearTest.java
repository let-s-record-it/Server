package com.sillim.recordit.task.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidMonthOfYearException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskMonthOfYearTest {

	@Test
	@DisplayName("1과 12사이의 수를 사용하여 MonthOfYear를 생성할 수 있다.")
	void validIfMonthOfYearIs1OrOverAnd12OrUnder() {
		TaskMonthOfYear monthOfYear1 = new TaskMonthOfYear(1);
		TaskMonthOfYear monthOfYear2 = new TaskMonthOfYear(12);
		assertAll(
				() -> {
					assertThat(monthOfYear1).isEqualTo(new TaskMonthOfYear(1));
					assertThat(monthOfYear2).isEqualTo(new TaskMonthOfYear(12));
					assertThat(monthOfYear1.getMonthOfYear()).isEqualTo(1);
					assertThat(monthOfYear2.getMonthOfYear()).isEqualTo(12);
				});
	}

	@Test
	@DisplayName("monthOfYear 값이 null이라면 InvalidMonthOfYearException이 발생한다.")
	void throwInvalidMonthOfYearExceptionIfMonthOfYearIsNull() {
		assertThatCode(() -> new TaskMonthOfYear(null))
				.isInstanceOf(InvalidMonthOfYearException.class)
				.hasMessage(ErrorCode.NULL_TASK_MONTH_OF_YEAR.getDescription());
	}

	@Test
	@DisplayName("monthOfYear 값이 1미만이면 InvalidMonthOfYearException이 발생한다.")
	void throwInvalidMonthOfYearExceptionIf1Under() {
		assertThatCode(() -> new TaskMonthOfYear(0))
				.isInstanceOf(InvalidMonthOfYearException.class)
				.hasMessage(ErrorCode.TASK_MONTH_OF_YEAR_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("monthOfYear 값이 12초과이면 InvalidMonthOfYearException이 발생한다.")
	void throwInvalidMonthOfYearExceptionIf12Over() {
		assertThatCode(() -> new TaskMonthOfYear(13))
				.isInstanceOf(InvalidMonthOfYearException.class)
				.hasMessage(ErrorCode.TASK_MONTH_OF_YEAR_OUT_OF_RANGE.getDescription());
	}
}
