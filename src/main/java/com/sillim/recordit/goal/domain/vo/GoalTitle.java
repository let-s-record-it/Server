package com.sillim.recordit.goal.domain.vo;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class GoalTitle {

	private static final int TITLE_LENGTH_MAX = 30;

	@Column(nullable = false, length = TITLE_LENGTH_MAX)
	private final String title;

	public GoalTitle(final String title) {
		validate(title);
		this.title = title;
	}

	private void validate(final String title) {

		if (Objects.isNull(title)) {
			throw new InvalidTitleException(ErrorCode.NULL_GOAL_TITLE);
		}
		if (title.isBlank()) {
			throw new InvalidTitleException(ErrorCode.BLANK_GOAL_TITLE);
		}
		if (title.length() > TITLE_LENGTH_MAX) {
			throw new InvalidTitleException(ErrorCode.INVALID_GOAL_TITLE_LENGTH);
		}
	}
}
