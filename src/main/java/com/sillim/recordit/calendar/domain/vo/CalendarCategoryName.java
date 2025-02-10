package com.sillim.recordit.calendar.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidCategoryNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarCategoryName {

	private static final int MAX_CATEGORY_NAME_LENGTH = 10;

	@Column(nullable = false, length = MAX_CATEGORY_NAME_LENGTH)
	private String name;

	public CalendarCategoryName(String name) {
		validate(name);
		this.name = name;
	}

	private void validate(String name) {
		if (Objects.isNull(name)) {
			throw new InvalidCategoryNameException(ErrorCode.NULL_CALENDAR_CATEGORY_NAME);
		}

		if (name.isBlank()) {
			throw new InvalidCategoryNameException(ErrorCode.BLANK_CALENDAR_CATEGORY_NAME);
		}

		if (name.length() > MAX_CATEGORY_NAME_LENGTH) {
			throw new InvalidCategoryNameException(ErrorCode.INVALID_CALENDAR_CATEGORY_NAME_LENGTH);
		}
	}
}
