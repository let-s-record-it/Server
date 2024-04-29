package com.sillim.recordit.calendar.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidColorHexException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CalendarColorHexTest {

	@Test
	@DisplayName("color hex 값에 맞는 ColorHex를 생성할 수 있다.")
	void validIfFitColorHex() {
		CalendarColorHex calendarColorHex1 = new CalendarColorHex("112233");
		CalendarColorHex calendarColorHex2 = new CalendarColorHex("ffda12fb");
		CalendarColorHex calendarColorHex3 = new CalendarColorHex("fab");

		assertAll(
				() -> {
					assertThat(calendarColorHex1).isEqualTo(new CalendarColorHex("112233"));
					assertThat(calendarColorHex2).isEqualTo(new CalendarColorHex("ffda12fb"));
					assertThat(calendarColorHex3).isEqualTo(new CalendarColorHex("fab"));
					assertThat(calendarColorHex1.getColorHex()).isEqualTo("112233");
					assertThat(calendarColorHex2.getColorHex()).isEqualTo("ffda12fb");
					assertThat(calendarColorHex3.getColorHex()).isEqualTo("fab");
				});
	}

	@Test
	@DisplayName("color hex 값에 맞지 않을 시 InvalidColorHexException이 발생한다.")
	void throwInvalidColorHexExceptionIfNotFitColorHex() {
		assertAll(
				() -> {
					assertThatCode(() -> new CalendarColorHex("11233"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_CALENDAR_COLOR_HEX.getDescription());
					assertThatCode(() -> new CalendarColorHex("ggg"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_CALENDAR_COLOR_HEX.getDescription());
					assertThatCode(() -> new CalendarColorHex("11233"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_CALENDAR_COLOR_HEX.getDescription());
					assertThatCode(() -> new CalendarColorHex("ffda12f"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_CALENDAR_COLOR_HEX.getDescription());
					assertThatCode(() -> new CalendarColorHex("fb"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_CALENDAR_COLOR_HEX.getDescription());
				});
	}
}
