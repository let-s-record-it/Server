package com.sillim.recordit.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.global.validation.goal.YearValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class YearValidatorTest {

	private final YearValidator validator = new YearValidator();
	private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
	private final ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);

	@BeforeEach
	void beforeEach() {
		willDoNothing().given(context).disableDefaultConstraintViolation();
		given(context.getDefaultConstraintMessageTemplate()).willReturn("");
		given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builder);
		given(builder.addConstraintViolation()).willReturn(context);
	}

	@Test
	@DisplayName("연도 값이 1900과 (현재 연도 + 25) 사이라면 true를 반환한다.")
	void yearValidatorTrueTest() {
		Integer year1 = 1900;
		Integer year2 = LocalDate.now().getYear();
		Integer year3 = LocalDate.now().getYear() + 25;
		assertAll(() -> {
			assertThat(validator.isValid(year1, context)).isTrue();
			assertThat(validator.isValid(year2, context)).isTrue();
			assertThat(validator.isValid(year3, context)).isTrue();
		});
	}

	@Test
	@DisplayName("연도 값이 1900 보다 작을 경우 false를 반환한다.")
	void yearValidatorLessThanMinTest() {
		Integer year = 1899;
		assertThat(validator.isValid(year, context)).isFalse();
	}

	@Test
	@DisplayName("연도 값이 (현재 연도 + 25) 보다 클 경우 false를 반환한다.")
	void yearValidatorGreaterThanMinTest() {
		Integer year = LocalDate.now().getYear() + 26;
		assertThat(validator.isValid(year, context)).isFalse();
	}

	@Test
	@DisplayName("연도 값이 null일 경우 false를 반환한다.")
	void yearValidatorNullTest() {
		Integer year = null;
		assertThat(validator.isValid(year, context)).isFalse();
	}
}
