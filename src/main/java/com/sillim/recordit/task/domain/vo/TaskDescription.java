package com.sillim.recordit.task.domain.vo;

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
public final class TaskDescription {

	private static final int MAX_DESCRIPTION_LENGTH = 500;

	@Column(nullable = false, length = MAX_DESCRIPTION_LENGTH)
	private final String description;

	public TaskDescription(final String description) {
		validate(description);
		this.description = description;
	}

	private void validate(final String description) {
		if (Objects.isNull(description)) {
			throw new InvalidDescriptionException(ErrorCode.NULL_TASK_DESCRIPTION);
		}
		if (description.isBlank()) {
			throw new InvalidDescriptionException(ErrorCode.BLANK_TASK_DESCRIPTION);
		}
		if (description.length() > MAX_DESCRIPTION_LENGTH) {
			throw new InvalidDescriptionException(ErrorCode.INVALID_SCHEDULE_DESCRIPTION_LENGTH);
		}
	}
}
