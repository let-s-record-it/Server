package com.sillim.recordit.feed.domain;

import com.sillim.recordit.feed.domain.vo.FeedImageUrl;
import com.sillim.recordit.global.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedImage extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, name = "feed_image_id")
	private Long id;

	@Embedded private FeedImageUrl imageUrl;

	@Column(nullable = false)
	@ColumnDefault("false")
	private Boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feed_id")
	private Feed feed;

	public FeedImage(String imageUrl, Feed feed) {
		this.imageUrl = new FeedImageUrl(imageUrl);
		this.feed = feed;
		this.deleted = false;
	}

	public String getImageUrl() {
		return imageUrl.getImageUrl();
	}
}
