package com.sillim.recordit.feed.domain;

import com.sillim.recordit.feed.domain.vo.FeedImageUrl;
import com.sillim.recordit.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, name = "feed_image_id")
	private Long id;

	@Embedded private FeedImageUrl imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "feed_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Feed feed;

	public FeedImage(String imageUrl, Feed feed) {
		this.imageUrl = new FeedImageUrl(imageUrl);
		this.feed = feed;
	}

	public String getImageUrl() {
		return imageUrl.getImageUrl();
	}
}
