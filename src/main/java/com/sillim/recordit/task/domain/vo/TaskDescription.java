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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskDescription {

	private static final int MAX_DESCRIPTION_LENGTH = 500;

	@Column(nullable = false, length = MAX_DESCRIPTION_LENGTH)
	private String description;

	public TaskDescription(String description) {
		validate(description);
		this.description = description;
	}

	private void validate(String description) {
		if (Objects.isNull(description)) {
			throw new InvalidDescriptionException(ErrorCode.NULL_SCHEDULE_DESCRIPTION);
		}

		if (description.length() > MAX_DESCRIPTION_LENGTH) {
			throw new InvalidDescriptionException(ErrorCode.INVALID_SCHEDULE_DESCRIPTION_LENGTH);
		}
	}
}
