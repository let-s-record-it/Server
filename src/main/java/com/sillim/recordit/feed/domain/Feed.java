package com.sillim.recordit.feed.domain;

import com.sillim.recordit.feed.domain.vo.FeedContent;
import com.sillim.recordit.feed.domain.vo.FeedImages;
import com.sillim.recordit.feed.domain.vo.FeedTitle;
import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.global.exception.feed.InvalidFeedLikeException;
import com.sillim.recordit.member.domain.Member;
import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Entity
@DynamicInsert
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

	@Column(nullable = false)
	private Long likeCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Embedded private FeedImages feedImages;

	@Version private Long version;

	public Feed(String title, String content, Member member, List<String> feedImageUrls) {
		this.title = new FeedTitle(title);
		this.content = new FeedContent(content);
		this.member = member;
		this.likeCount = 0L;
		this.deleted = false;
		this.feedImages =
				new FeedImages(
						feedImageUrls.stream()
								.map(feedImageUrl -> new FeedImage(feedImageUrl, this))
								.collect(Collectors.toList()));
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

	public List<String> getFeedImageUrls() {
		return feedImages.getFeedImages().stream().map(FeedImage::getImageUrl).toList();
	}

	public void modify(
			String title,
			String content,
			List<String> existingImageUrls,
			List<String> newImageUrls) {
		this.title = new FeedTitle(title);
		this.content = new FeedContent(content);
		feedImages.modifyFeedImages(
				existingImageUrls,
				newImageUrls.stream()
						.map(newImageUrl -> new FeedImage(newImageUrl, this))
						.collect(Collectors.toList()));
	}

	public boolean isOwner(Long memberId) {
		return this.member.equalsId(memberId);
	}

	public void validateAuthenticatedUser(Long memberId) {
		if (!isOwner(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}
	}

	public void like() {
		this.likeCount++;
	}

	public void unlike() {
		if (likeCount <= 0) {
			throw new InvalidFeedLikeException(ErrorCode.INVALID_FEED_UNLIKE);
		}
		this.likeCount--;
	}

	public void delete() {
		this.deleted = true;
	}
}
