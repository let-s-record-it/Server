package com.sillim.recordit.feed.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.feed.InvalidFeedImageUrlException;
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
public class FeedImageUrl {

	@Column(nullable = false)
	private String imageUrl;

	public FeedImageUrl(String imageUrl) {
		validate(imageUrl);
		this.imageUrl = imageUrl;
	}

	private void validate(String imageUrl) {
		if (Objects.isNull(imageUrl)) {
			throw new InvalidFeedImageUrlException(ErrorCode.NULL_FEED_IMAGE_URL);
		}

		if (imageUrl.isBlank()) {
			throw new InvalidFeedImageUrlException(ErrorCode.BLANK_FEED_IMAGE_URL);
		}
	}
}
