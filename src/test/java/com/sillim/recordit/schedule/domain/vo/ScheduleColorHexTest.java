package com.sillim.recordit.schedule.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidColorHexException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduleColorHexTest {

	@Test
	@DisplayName("color hex 값에 맞는 ColorHex를 생성할 수 있다.")
	void validIfFitColorHex() {
		ScheduleColorHex scheduleColorHex1 = new ScheduleColorHex("112233");
		ScheduleColorHex scheduleColorHex2 = new ScheduleColorHex("ffda12fb");
		ScheduleColorHex scheduleColorHex3 = new ScheduleColorHex("fab");

		assertAll(
				() -> {
					assertThat(scheduleColorHex1).isEqualTo(new ScheduleColorHex("112233"));
					assertThat(scheduleColorHex2).isEqualTo(new ScheduleColorHex("ffda12fb"));
					assertThat(scheduleColorHex3).isEqualTo(new ScheduleColorHex("fab"));
					assertThat(scheduleColorHex1.getColorHex()).isEqualTo("112233");
					assertThat(scheduleColorHex2.getColorHex()).isEqualTo("ffda12fb");
					assertThat(scheduleColorHex3.getColorHex()).isEqualTo("fab");
				});
	}

	@Test
	@DisplayName("color hex 값에 맞지 않을 시 InvalidColorHexException이 발생한다.")
	void throwInvalidColorHexExceptionIfNotFitColorHex() {
		assertAll(
				() -> {
					assertThatCode(() -> new ScheduleColorHex("11233"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_SCHEDULE_COLOR_HEX.getDescription());
					assertThatCode(() -> new ScheduleColorHex("ggg"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_SCHEDULE_COLOR_HEX.getDescription());
					assertThatCode(() -> new ScheduleColorHex("11233"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_SCHEDULE_COLOR_HEX.getDescription());
					assertThatCode(() -> new ScheduleColorHex("ffda12f"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_SCHEDULE_COLOR_HEX.getDescription());
					assertThatCode(() -> new ScheduleColorHex("fb"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_SCHEDULE_COLOR_HEX.getDescription());
				});
	}
}
