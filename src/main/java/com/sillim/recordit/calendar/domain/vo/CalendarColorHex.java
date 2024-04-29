package com.sillim.recordit.calendar.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidColorHexException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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

	private static final String COLOR_HEX_REGEX = "#[0-9a-fA-F]{8}|#[0-9a-fA-F]{6}|#[0-9a-fA-F]{3}";

	@Column(nullable = false)
	private String colorHex;

	public CalendarColorHex(String colorHex) {
		validate(colorHex);
		this.colorHex = colorHex;
	}

	private void validate(final String colorHex) {
		if (!Pattern.matches(COLOR_HEX_REGEX, colorHex)) {
			throw new InvalidColorHexException(ErrorCode.INVALID_SCHEDULE_COLOR_HEX);
		}
	}
}
