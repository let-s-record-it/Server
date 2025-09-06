package com.sillim.recordit.calendar.domain;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CalendarTest {

	@Test
	@DisplayName("캘린더 주인이 아니면 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionIfNotCalendarOwner() {
		long memberId = 1L;
		Calendar calendar =
				new Calendar(
						"title",
						CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId),
						memberId);

		assertThatCode(() -> calendar.validateAuthenticatedMember(2L))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}
}
