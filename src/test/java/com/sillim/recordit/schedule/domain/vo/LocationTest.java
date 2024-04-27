package com.sillim.recordit.schedule.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidLocationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationTest {

	@Test
	@DisplayName("0 ~ 90 위도, 0 ~ 180 경도를 가진 Location을 생성할 수 있다.")
	void validIfLatitude0OrOver90OrUnderAndLongitude0OrOverAnd180OrUnder() {
		Location location1 = Location.create(0, 0);
		Location location2 = Location.create(90, 180);
		assertAll(
				() -> {
					assertThat(location1).isEqualTo(Location.create(0, 0));
					assertThat(location2).isEqualTo(Location.create(90, 180));
					assertThat(location1.getLatitude())
							.isEqualByComparingTo(BigDecimal.valueOf(0L));
					assertThat(location1.getLongitude())
							.isEqualByComparingTo(BigDecimal.valueOf(0L));
					assertThat(location2.getLatitude())
							.isEqualByComparingTo(BigDecimal.valueOf(90L));
					assertThat(location2.getLongitude())
							.isEqualByComparingTo(BigDecimal.valueOf(180L));
				});
	}

	@Test
	@DisplayName("위도가 0 미만이면 InvalidLocationException이 발생한다.")
	void throwInvalidLocationExceptionIfLatitudeIs0Under() {
		assertThatCode(() -> Location.create(-1, 0))
				.isInstanceOf(InvalidLocationException.class)
				.hasMessage(ErrorCode.LATITUDE_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("위도가 90 초과이면 InvalidLocationException이 발생한다.")
	void throwInvalidLocationExceptionIfLatitudeIs90Over() {
		assertThatCode(() -> Location.create(91, 0))
				.isInstanceOf(InvalidLocationException.class)
				.hasMessage(ErrorCode.LATITUDE_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("경도가 0 미만이면 InvalidLocationException이 발생한다.")
	void throwInvalidLocationExceptionIfLongitudeIs0Under() {
		assertThatCode(() -> Location.create(0, -1))
				.isInstanceOf(InvalidLocationException.class)
				.hasMessage(ErrorCode.LONGITUDE_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("경도가 180 초과이면 InvalidLocationException이 발생한다.")
	void throwInvalidLocationExceptionIfLongitudeIs180Over() {
		assertThatCode(() -> Location.create(0, 181))
				.isInstanceOf(InvalidLocationException.class)
				.hasMessage(ErrorCode.LONGITUDE_OUT_OF_RANGE.getDescription());
	}
}
