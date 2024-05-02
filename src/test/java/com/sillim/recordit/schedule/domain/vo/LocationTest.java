package com.sillim.recordit.schedule.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidLocationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationTest {

	@Test
	@DisplayName("0 ~ 90 위도, 0 ~ 180 경도를 가진 Location을 생성할 수 있다.")
	void validIfLatitude0OrOver90OrUnderAndLongitude0OrOverAnd180OrUnder() {
		Location location1 = new Location(0, 0);
		Location location2 = new Location(90, 180);
		assertAll(
				() -> {
					assertThat(location1).isEqualTo(new Location(0, 0));
					assertThat(location2).isEqualTo(new Location(90, 180));
					assertThat(location1.getLatitude()).isEqualTo(0);
					assertThat(location1.getLongitude()).isEqualTo(0);
					assertThat(location2.getLatitude()).isEqualTo(90);
					assertThat(location2.getLongitude()).isEqualTo(180);
				});
	}

	@Test
	@DisplayName("위도가 0 미만이면 InvalidLocationException이 발생한다.")
	void throwInvalidLocationExceptionIfLatitudeIs0Under() {
		assertThatCode(() -> new Location(-1, 0))
				.isInstanceOf(InvalidLocationException.class)
				.hasMessage(ErrorCode.LATITUDE_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("위도가 90 초과이면 InvalidLocationException이 발생한다.")
	void throwInvalidLocationExceptionIfLatitudeIs90Over() {
		assertThatCode(() -> new Location(91, 0))
				.isInstanceOf(InvalidLocationException.class)
				.hasMessage(ErrorCode.LATITUDE_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("경도가 0 미만이면 InvalidLocationException이 발생한다.")
	void throwInvalidLocationExceptionIfLongitudeIs0Under() {
		assertThatCode(() -> new Location(0, -1))
				.isInstanceOf(InvalidLocationException.class)
				.hasMessage(ErrorCode.LONGITUDE_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("경도가 180 초과이면 InvalidLocationException이 발생한다.")
	void throwInvalidLocationExceptionIfLongitudeIs180Over() {
		assertThatCode(() -> new Location(0, 181))
				.isInstanceOf(InvalidLocationException.class)
				.hasMessage(ErrorCode.LONGITUDE_OUT_OF_RANGE.getDescription());
	}
}
