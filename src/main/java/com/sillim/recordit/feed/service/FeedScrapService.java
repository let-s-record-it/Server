package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.FeedScrap;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.feed.repository.FeedScrapRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedScrapService {

	private final FeedScrapRepository feedScrapRepository;
	private final FeedRepository feedRepository;
	private final MemberQueryService memberQueryService;

	public void feedScrap(Long feedId, Long memberId) {
		feedScrapRepository.save(
				new FeedScrap(
						feedRepository
								.findById(feedId)
								.orElseThrow(
										() ->
												new RecordNotFoundException(
														ErrorCode.FEED_NOT_FOUND)),
						memberQueryService.findByMemberId(memberId)));
	}

	public void feedUnScrap(Long feedId, Long memberId) {
		feedScrapRepository.deleteByFeedIdAndMemberId(feedId, memberId);
	}
}
