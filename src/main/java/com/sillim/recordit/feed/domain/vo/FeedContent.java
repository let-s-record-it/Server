package com.sillim.recordit.feed.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.feed.InvalidFeedContentException;
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
public class FeedContent {

	private static final int MAX_CONTENT_LENGTH = 5000;

	@Column(nullable = false, length = MAX_CONTENT_LENGTH)
	private String content;

	public FeedContent(String content) {
		validate(content);
		this.content = content;
	}

	private void validate(String content) {
		if (Objects.isNull(content)) {
			throw new InvalidFeedContentException(ErrorCode.NULL_FEED_CONTENT);
		}

		if (content.length() > MAX_CONTENT_LENGTH) {
			throw new InvalidFeedContentException(ErrorCode.INVALID_FEED_CONTENT_LENGTH);
		}
	}
}
