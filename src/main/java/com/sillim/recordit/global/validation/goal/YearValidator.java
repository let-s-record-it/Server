package com.sillim.recordit.global.validation.goal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class YearValidator implements ConstraintValidator<ValidYear, Integer> {
	@Override
	public boolean isValid(Integer year, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation(); // default message 비활성화
		context.buildConstraintViolationWithTemplate(
						String.format(
								context.getDefaultConstraintMessageTemplate(),
								LocalDate.now().getYear() + 25))
				.addConstraintViolation(); // 새로운 message 할당
		return year >= 1900 && year <= LocalDate.now().getYear() + 25;
	}
}
