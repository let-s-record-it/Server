package com.sillim.recordit.feed.dto.response;

import com.sillim.recordit.feed.domain.FeedComment;
import com.sillim.recordit.member.domain.Member;
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

	public static FeedCommentInListResponse from(
			FeedComment feedComment, Member owner, Long memberId) {
		return FeedCommentInListResponse.builder()
				.id(feedComment.getId())
				.content(feedComment.getContent())
				.createdAt(feedComment.getCreatedAt())
				.memberId(owner.getId())
				.memberName(owner.getName())
				.memberJob(owner.getJob())
				.memberProfileImageUrl(owner.getProfileImageUrl())
				.isOwner(feedComment.isOwner(memberId))
				.build();
	}
}
