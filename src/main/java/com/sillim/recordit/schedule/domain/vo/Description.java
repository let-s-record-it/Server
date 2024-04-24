package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidDescriptionException;
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
public class Description {

	@Column(nullable = false)
	private String description;

	public Description(String description) {
		this.description = description;
	}

	private void validate(String description) {
		if (Objects.isNull(description)) {
			throw new InvalidDescriptionException(
					ErrorCode.INVALID_DESCRIPTION, "설명은 null일 수 없습니다.");
		}

		if (description.length() > 500) {
			throw new InvalidDescriptionException(
					ErrorCode.INVALID_DESCRIPTION, "설명의 길이는 500자를 넘을 수 없습니다.");
		}
	}
}
