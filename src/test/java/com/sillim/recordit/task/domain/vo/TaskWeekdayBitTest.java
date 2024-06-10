package com.sillim.recordit.task.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidWeekdayBitException;
import java.time.DayOfWeek;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskWeekdayBitTest {

	@Test
	@DisplayName("0부터 127까지의 수를 사용해서 WeekdayBit를 생성할 수 있다.")
	void validIfWeekdayBit0OrOverAnd127OrUnder() {
		TaskWeekdayBit weekdayBit1 = new TaskWeekdayBit(Integer.parseInt("0000000", 2));
		TaskWeekdayBit weekdayBit2 = new TaskWeekdayBit(Integer.parseInt("1111111", 2));

		assertAll(
				() -> {
					assertThat(weekdayBit1)
							.isEqualTo(new TaskWeekdayBit(Integer.parseInt("0000000", 2)));
					assertThat(weekdayBit1).isEqualTo(new TaskWeekdayBit(0));
					assertThat(weekdayBit2)
							.isEqualTo(new TaskWeekdayBit(Integer.parseInt("1111111", 2)));
					assertThat(weekdayBit2).isEqualTo(new TaskWeekdayBit(127));
				});
	}

	@Test
	@DisplayName("WeekdayBit가 null이라면 InvalidWeekdayBitException이 발생한다.")
	void throwInvalidWeekdayBitExceptionIfWeekdayBitIsNull() {
		assertThatCode(() -> new TaskWeekdayBit(null))
				.isInstanceOf(InvalidWeekdayBitException.class)
				.hasMessage(ErrorCode.NULL_TASK_WEEKDAY_BIT.getDescription());
	}

	@Test
	@DisplayName("WeekdayBit가 0미만일 경우 InvalidWeekdayBitException 예외가 발생한다.")
	void throwInvalidWeekdayBitExceptionIfWeekdayBitIs1Under() {
		assertThatCode(() -> new TaskWeekdayBit(-1))
				.isInstanceOf(InvalidWeekdayBitException.class)
				.hasMessage(ErrorCode.TASK_WEEKDAY_BIT_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("WeekdayBit가 127초과일 경우 InvalidWeekdayBitException 예외가 발생한다.")
	void throwInvalidWeekdayBitExceptionWhen127OverWeekdayBit() {
		assertThatCode(() -> new TaskWeekdayBit(128))
				.isInstanceOf(InvalidWeekdayBitException.class)
				.hasMessage(ErrorCode.TASK_WEEKDAY_BIT_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("Weekday값을 통해 반복 설정된 비트를 찾을 수 있다.")
	void findValidBitByWeekday() {
		TaskWeekdayBit weekdayBit = new TaskWeekdayBit(Integer.parseInt("1001001", 2));

		assertThat(weekdayBit.isValidWeekday(DayOfWeek.MONDAY)).isTrue();
		assertThat(weekdayBit.isValidWeekday(DayOfWeek.THURSDAY)).isTrue();
		assertThat(weekdayBit.isValidWeekday(DayOfWeek.SUNDAY)).isTrue();
	}
}
