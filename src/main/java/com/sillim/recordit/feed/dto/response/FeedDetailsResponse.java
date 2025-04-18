package com.sillim.recordit.feed.dto.response;

import com.sillim.recordit.feed.domain.Feed;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record FeedDetailsResponse(
		Long id,
		String title,
		String content,
		LocalDateTime createdAt,
		List<String> feedImageUrls,
		long likeCount,
		boolean isLiked,
		boolean isScraped,
		String memberName,
		String memberJob,
		String memberProfileImageUrl,
		boolean isOwner) {

	public static FeedDetailsResponse of(
			Feed feed, Long memberId, boolean isLiked, boolean isScraped) {
		return FeedDetailsResponse.builder()
				.id(feed.getId())
				.title(feed.getTitle())
				.content(feed.getContent())
				.createdAt(feed.getCreatedAt())
				.feedImageUrls(feed.getFeedImageUrls())
				.likeCount(feed.getLikeCount())
				.isLiked(isLiked)
				.isScraped(isScraped)
				.memberName(feed.getMember().getName())
				.memberJob(feed.getMember().getJob())
				.memberProfileImageUrl(feed.getMember().getProfileImageUrl())
				.isOwner(feed.isOwner(memberId))
				.build();
	}
}
