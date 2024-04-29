package com.sillim.recordit.calendar.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidColorHexException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarColorHex {

	private static final String COLOR_HEX_REGEX = "[0-9a-fA-F]{8}|[0-9a-fA-F]{6}|[0-9a-fA-F]{3}";

	@Column(nullable = false)
	private String colorHex;

	public CalendarColorHex(String colorHex) {
		validate(colorHex);
		this.colorHex = colorHex;
	}

	private void validate(final String colorHex) {
		if (Objects.isNull(colorHex)) {
			throw new InvalidColorHexException(ErrorCode.NULL_CALENDAR_COLOR_HEX);
		}

		if (!Pattern.matches(COLOR_HEX_REGEX, colorHex)) {
			throw new InvalidColorHexException(ErrorCode.INVALID_CALENDAR_COLOR_HEX);
		}
	}
}
