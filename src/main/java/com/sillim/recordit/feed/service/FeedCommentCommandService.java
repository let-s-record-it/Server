package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedComment;
import com.sillim.recordit.feed.dto.request.FeedCommentAddRequest;
import com.sillim.recordit.feed.repository.FeedCommentRepository;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedCommentCommandService {

	private final FeedCommentRepository feedCommentRepository;
	private final FeedRepository feedRepository;
	private final MemberQueryService memberQueryService;

	public Long addFeedComment(FeedCommentAddRequest request, Long feedId, Long memberId) {
		Feed feed =
				feedRepository
						.findById(feedId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND));
		Member member = memberQueryService.findByMemberId(memberId);
		return feedCommentRepository.save(new FeedComment(request.content(), feed, member)).getId();
	}

	public void removeFeedComment(Long commentId, Long memberId) {
		FeedComment feedComment =
				feedCommentRepository
						.findByIdWithFetch(commentId)
						.orElseThrow(
								() ->
										new RecordNotFoundException(
												ErrorCode.FEED_COMMENT_NOT_FOUND));
		feedComment.validateAuthenticatedUser(memberId);
		feedComment.delete();
	}
}
