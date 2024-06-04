package com.sillim.recordit.global.validation.goal;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.validator.constraints.Length;

@NotBlank
@Length(max = 500) @Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidDescription {
	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
