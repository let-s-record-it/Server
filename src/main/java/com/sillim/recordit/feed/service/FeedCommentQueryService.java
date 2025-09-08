package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.FeedComment;
import com.sillim.recordit.feed.dto.response.FeedCommentInListResponse;
import com.sillim.recordit.feed.repository.FeedCommentRepository;
import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedCommentQueryService {

	private final FeedCommentRepository feedCommentRepository;
	private final MemberQueryService memberQueryService;

	public FeedCommentInListResponse searchFeedCommentById(Long commentId, Long memberId) {
		FeedComment feedComment = feedCommentRepository.findByIdWithFetch(commentId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_COMMENT_NOT_FOUND));
		return FeedCommentInListResponse.from(feedComment, memberQueryService.findByMemberId(feedComment.getMemberId()),
				memberId);
	}

	public SliceResponse<FeedCommentInListResponse> searchPaginatedOldCreated(Pageable pageable, Long feedId,
			Long memberId) {
		Slice<FeedComment> feedCommentSlice = feedCommentRepository.findPaginatedOrderByCreatedAtAsc(pageable, feedId);
		return SliceResponse.of(new SliceImpl<>(
				feedCommentSlice.stream()
						.map(feedComment -> FeedCommentInListResponse.from(feedComment,
								memberQueryService.findByMemberId(feedComment.getMemberId()), memberId))
						.toList(),
				pageable, feedCommentSlice.hasNext()));
	}

	public SliceResponse<FeedCommentInListResponse> searchByMemberIdOldCreated(Pageable pageable, Long memberId) {
		Slice<FeedComment> feedCommentSlice = feedCommentRepository.findByMemberIdOrderByCreatedAtAsc(pageable,
				memberId);
		return SliceResponse.of(new SliceImpl<>(
				feedCommentSlice.stream()
						.map(feedComment -> FeedCommentInListResponse.from(feedComment,
								memberQueryService.findByMemberId(feedComment.getMemberId()), memberId))
						.toList(),
				pageable, feedCommentSlice.hasNext()));
	}
}
