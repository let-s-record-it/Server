package com.sillim.recordit.feed.dto.response;

import com.sillim.recordit.feed.domain.FeedComment;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FeedCommentInListResponse(
		long id,
		String content,
		LocalDateTime createdAt,
		long memberId,
		String memberName,
		String memberJob,
		String memberProfileImageUrl,
		boolean isOwner) {

	public static FeedCommentInListResponse from(FeedComment feedComment, Long memberId) {
		return FeedCommentInListResponse.builder()
				.id(feedComment.getId())
				.content(feedComment.getContent())
				.createdAt(feedComment.getCreatedAt())
				.memberId(feedComment.getMember().getId())
				.memberName(feedComment.getMember().getName())
				.memberJob(feedComment.getMember().getJob())
				.memberProfileImageUrl(feedComment.getMember().getProfileImageUrl())
				.isOwner(feedComment.isOwner(memberId))
				.build();
	}
}
