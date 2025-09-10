package com.sillim.recordit.feed.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.feed.InvalidFeedCommentContentException;
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
public class FeedCommentContent {

	private static final int MAX_CONTENT_LENGTH = 1000;

	@Column(nullable = false, length = MAX_CONTENT_LENGTH)
	private String content;

	public FeedCommentContent(String content) {
		validate(content);
		this.content = content;
	}

	private void validate(String content) {
		if (Objects.isNull(content)) {
			throw new InvalidFeedCommentContentException(ErrorCode.NULL_FEED_COMMENT_CONTENT);
		}

		if (content.length() > MAX_CONTENT_LENGTH) {
			throw new InvalidFeedCommentContentException(
					ErrorCode.INVALID_FEED_COMMENT_CONTENT_LENGTH);
		}
	}
}
