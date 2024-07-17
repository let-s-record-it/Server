package com.sillim.recordit.feed.dto.response;

import com.sillim.recordit.feed.domain.Feed;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record FeedInListResponse(
		Long id,
		String title,
		String content,
		LocalDateTime createdAt,
		List<String> feedImageUrls,
		String memberName,
		String memberJob) {

	public static FeedInListResponse from(Feed feed) {
		return FeedInListResponse.builder()
				.id(feed.getId())
				.title(feed.getTitle())
				.content(feed.getContent())
				.createdAt(feed.getCreatedAt())
				.feedImageUrls(feed.getFeedImageUrls())
				.memberName(feed.getMember().getName())
				.memberJob(feed.getMember().getJob())
				.build();
	}
}
