package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.response.FeedInListResponse;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
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

	public Feed searchById(Long feedId) {
		return feedRepository
				.findByIdWithFetchJoin(feedId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND));
	}

	public SliceResponse<FeedInListResponse> searchPaginatedRecentCreated(Pageable pageable) {
		Slice<Feed> feedSlice = feedRepository.findPaginatedOrderByCreatedAtDesc(pageable);

		return SliceResponse.of(
				new SliceImpl<>(
						feedSlice.stream().map(FeedInListResponse::from).toList(),
						pageable,
						feedSlice.hasNext()));
	}
}
