package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.response.FeedDetailsResponse;
import com.sillim.recordit.feed.dto.response.FeedInListResponse;
import com.sillim.recordit.feed.repository.FeedRepository;
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
public class FeedQueryService {

	private final FeedRepository feedRepository;
	private final FeedLikeQueryService feedLikeQueryService;
	private final FeedScrapQueryService feedScrapQueryService;
	private final MemberQueryService memberQueryService;

	public FeedDetailsResponse searchById(Long feedId, Long memberId) {
		Feed feed =
				feedRepository
						.findByIdWithFetchJoin(feedId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND));
		return FeedDetailsResponse.of(
				feed,
				memberQueryService.findByMemberId(feed.getMemberId()),
				memberId,
				feedLikeQueryService.isLiked(feedId, memberId),
				feedScrapQueryService.isScraped(feedId, memberId));
	}

	public SliceResponse<FeedInListResponse> searchRecentCreated(Pageable pageable, Long memberId) {
		Slice<Feed> feedSlice = feedRepository.findOrderByCreatedAtDesc(pageable);

		return SliceResponse.of(
				new SliceImpl<>(
						feedSlice.stream()
								.map(
										feed ->
												FeedInListResponse.from(
														feed,
														memberQueryService.findByMemberId(
																feed.getMemberId()),
														feedLikeQueryService.isLiked(
																feed.getId(), memberId),
														feedScrapQueryService.isScraped(
																feed.getId(), memberId)))
								.toList(),
						pageable,
						feedSlice.hasNext()));
	}

	public SliceResponse<FeedInListResponse> searchRecentCreatedByMemberId(
			Pageable pageable, Long memberId) {
		Slice<Feed> feedSlice =
				feedRepository.findByMemberIdOrderByCreatedAtDesc(pageable, memberId);

		return SliceResponse.of(
				new SliceImpl<>(
						feedSlice.stream()
								.map(
										feed ->
												FeedInListResponse.from(
														feed,
														memberQueryService.findByMemberId(
																feed.getMemberId()),
														feedLikeQueryService.isLiked(
																feed.getId(), memberId),
														feedScrapQueryService.isScraped(
																feed.getId(), memberId)))
								.toList(),
						pageable,
						feedSlice.hasNext()));
	}
}
