package com.sillim.recordit.feed.domain;

import com.sillim.recordit.feed.domain.vo.FeedContent;
import com.sillim.recordit.feed.domain.vo.FeedImages;
import com.sillim.recordit.feed.domain.vo.FeedTitle;
import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.member.domain.Member;
import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, name = "feed_id")
	private Long id;

	@Embedded private FeedTitle title;

	@Embedded private FeedContent content;

	@Column(nullable = false)
	@ColumnDefault("false")
	private Boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Embedded private FeedImages feedImages;

	public Feed(String title, String content, Member member, List<String> feedImageUrls) {
		this.title = new FeedTitle(title);
		this.content = new FeedContent(content);
		this.member = member;
		this.feedImages =
				new FeedImages(
						feedImageUrls.stream()
								.map(feedImageUrl -> new FeedImage(feedImageUrl, this))
								.collect(Collectors.toList()));
		this.deleted = false;
	}

	public String getTitle() {
		return title.getTitle();
	}

	public String getContent() {
		return content.getContent();
	}

	public Integer getFeedImageCount() {
		return feedImages.getFeedImageCount();
	}

	public List<FeedImage> getFeedImages() {
		return feedImages.getFeedImages();
	}
}