package com.sillim.recordit.category.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidCategoryNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduleCategoryNameTest {

	@Test
	@DisplayName("10자 이내의 일정 카테고리 이름을 생성할 수 있다.")
	void createCalendarCategoryNameIfLengthUnder10() {
		ScheduleCategoryName name = new ScheduleCategoryName("이름");
		ScheduleCategoryName name2 = new ScheduleCategoryName("이름이름이름이름이름");

		assertThat(name).isEqualTo(new ScheduleCategoryName("이름"));
		assertThat(name2).isEqualTo(new ScheduleCategoryName("이름이름이름이름이름"));
	}

	@Test
	@DisplayName("일정 카테고리 이름이 null이면 InvalidCategoryNameException이 발생한다.")
	void throwInvalidCategoryNameExceptionWhenCreateCalendarCategoryNameIfNameIsNull() {
		assertThatCode(() -> new ScheduleCategoryName(null)).isInstanceOf(InvalidCategoryNameException.class)
				.hasMessage(ErrorCode.NULL_SCHEDULE_CATEGORY_NAME.getDescription());
	}

	@Test
	@DisplayName("일정 카테고리 이름이 공백이면 InvalidCategoryNameException이 발생한다.")
	void throwInvalidCategoryNameExceptionWhenCreateCalendarCategoryNameIfNameIsBlank() {
		assertThatCode(() -> new ScheduleCategoryName(" ")).isInstanceOf(InvalidCategoryNameException.class)
				.hasMessage(ErrorCode.BLANK_SCHEDULE_CATEGORY_NAME.getDescription());
	}

	@Test
	@DisplayName("일정 카테고리 이름이 10자를 넘으면 InvalidCategoryNameException이 발생한다.")
	void throwInvalidCategoryNameExceptionWhenCreateCalendarCategoryNameIfLengthOver10() {

		assertThatCode(() -> new ScheduleCategoryName("이름이름이름이름이름이")).isInstanceOf(InvalidCategoryNameException.class)
				.hasMessage(ErrorCode.INVALID_SCHEDULE_CATEGORY_NAME_LENGTH.getDescription());
	}
}
