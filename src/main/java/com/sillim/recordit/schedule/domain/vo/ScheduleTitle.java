package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidTitleException;
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
public class ScheduleTitle {

	private static final int TITLE_LENGTH_MAX = 30;

	@Column(nullable = false, length = TITLE_LENGTH_MAX)
	private String title;

	public ScheduleTitle(String title) {
		validate(title);
		this.title = title;
	}

	private void validate(String title) {
		if (Objects.isNull(title)) {
			throw new InvalidTitleException(ErrorCode.NULL_SCHEDULE_TITLE);
		}

		if (title.isBlank()) {
			throw new InvalidTitleException(ErrorCode.BLANK_SCHEDULE_TITLE);
		}

		if (title.length() > TITLE_LENGTH_MAX) {
			throw new InvalidTitleException(ErrorCode.INVALID_SCHEDULE_TITLE_LENGTH);
		}
	}
}
