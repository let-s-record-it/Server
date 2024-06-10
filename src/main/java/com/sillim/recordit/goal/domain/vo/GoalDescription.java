package com.sillim.recordit.goal.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidDescriptionException;
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
public class GoalDescription {

	private static final int DESCRIPTION_MAX_LENGTH = 500;

	@Column(nullable = false, length = DESCRIPTION_MAX_LENGTH)
	private final String description;

	public GoalDescription(final String description) {
		validate(description);
		this.description = description;
	}

	private void validate(final String description) {

		if (Objects.isNull(description)) {
			throw new InvalidDescriptionException(ErrorCode.NULL_GOAL_DESCRIPTION);
		}
		if (description.length() > DESCRIPTION_MAX_LENGTH) {
			throw new InvalidDescriptionException(ErrorCode.INVALID_GOAL_DESCRIPTION_LENGTH);
		}
	}
}
