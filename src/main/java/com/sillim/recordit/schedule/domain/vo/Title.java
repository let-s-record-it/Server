package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidTitleException;
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
public class Title {

	@Column(nullable = false)
	private String title;

	public Title(String title) {
		validate(title);
		this.title = title;
	}

	private void validate(String title) {
		if (Objects.isNull(title)) {
			throw new InvalidTitleException(ErrorCode.INVALID_TITLE, "제목은 null일 수 없습니다.");
		}

		if (title.isBlank()) {
			throw new InvalidTitleException(ErrorCode.INVALID_TITLE, "제목은 빈 값일 수 없습니다.");
		}

		if (title.length() > 30) {
			throw new InvalidTitleException(ErrorCode.INVALID_TITLE, "제목의 길이는 30자를 넘을 수 없습니다.");
		}
	}
}
