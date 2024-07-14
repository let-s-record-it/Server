package com.sillim.recordit.feed.domain.vo;

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
public class FeedTitle {

	private static final int MAX_TITLE_LENGTH = 30;

	@Column(nullable = false, length = MAX_TITLE_LENGTH)
	private String title;

	public FeedTitle(String title) {
		validate(title);
		this.title = title;
	}

	private void validate(String title) {
		if (Objects.isNull(title)) {
			throw new InvalidTitleException(ErrorCode.NULL_FEED_TITLE);
		}

		if (title.isBlank()) {
			throw new InvalidTitleException(ErrorCode.BLANK_FEED_TITLE);
		}

		if (title.length() > MAX_TITLE_LENGTH) {
			throw new InvalidTitleException(ErrorCode.INVALID_FEED_TITLE_LENGTH);
		}
	}
}
