package com.sillim.recordit.schedule.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidWeekdayBitException;
import java.time.DayOfWeek;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WeekdayBitTest {

	@Test
	@DisplayName("0부터 127까지의 수를 사용해서 WeekdayBit를 생성할 수 있다.")
	void validIfWeekdayBit0OrOverAnd127OrUnder() {
		WeekdayBit weekdayBit1 = new WeekdayBit(Integer.parseInt("0000000", 2));
		WeekdayBit weekdayBit2 = new WeekdayBit(Integer.parseInt("1111111", 2));

		assertAll(() -> {
			assertThat(weekdayBit1).isEqualTo(new WeekdayBit(Integer.parseInt("0000000", 2)));
			assertThat(weekdayBit1).isEqualTo(new WeekdayBit(0));
			assertThat(weekdayBit2).isEqualTo(new WeekdayBit(Integer.parseInt("1111111", 2)));
			assertThat(weekdayBit2).isEqualTo(new WeekdayBit(127));
		});
	}

	@Test
	@DisplayName("WeekdayBit가 0미만일 경우 InvalidWeekdayBitException 예외가 발생한다.")
	void throwInvalidWeekdayBitExceptionIfWeekdayBitIs1Under() {
		assertThatCode(() -> new WeekdayBit(-1)).isInstanceOf(InvalidWeekdayBitException.class)
				.hasMessage(ErrorCode.WEEKDAY_BIT_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("WeekdayBit가 127초과일 경우 InvalidWeekdayBitException 예외가 발생한다.")
	void throwInvalidWeekdayBitExceptionWhen127OverWeekdayBit() {
		assertThatCode(() -> new WeekdayBit(128)).isInstanceOf(InvalidWeekdayBitException.class)
				.hasMessage(ErrorCode.WEEKDAY_BIT_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("Weekday값을 통해 반복 설정된 비트를 찾을 수 있다.")
	void findValidBitByWeekday() {
		WeekdayBit weekdayBit = new WeekdayBit(Integer.parseInt("1001001", 2));

		assertThat(weekdayBit.isValidWeekday(DayOfWeek.MONDAY)).isTrue();
		assertThat(weekdayBit.isValidWeekday(DayOfWeek.THURSDAY)).isTrue();
		assertThat(weekdayBit.isValidWeekday(DayOfWeek.SUNDAY)).isTrue();
	}
}
