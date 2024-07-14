package com.sillim.recordit.feed.domain.vo;

import com.sillim.recordit.feed.domain.FeedImage;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.feed.InvalidFeedImageCountException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedImages {

	private static final int MAX_FEED_IMAGE_COUNT = 10;

	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer feedImageCount;

	@OneToMany(
			mappedBy = "feed",
			fetch = FetchType.LAZY,
			cascade = CascadeType.PERSIST,
			orphanRemoval = true)
	private List<FeedImage> feedImages = new ArrayList<>();

	public FeedImages(List<FeedImage> feedImages) {
		validateFeedImageCount(feedImages.size());
		this.feedImageCount = feedImages.size();
		this.feedImages = feedImages;
	}

	public List<FeedImage> getFeedImages() {
		return Collections.unmodifiableList(feedImages);
	}

	private void validateFeedImageCount(int feedImageCount) {
		if (feedImageCount > MAX_FEED_IMAGE_COUNT) {
			throw new InvalidFeedImageCountException(ErrorCode.OVER_FEED_IMAGE_COUNT);
		}
	}
}
