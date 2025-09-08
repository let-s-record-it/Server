package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedLike;
import com.sillim.recordit.feed.repository.FeedLikeRepository;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedLikeService {

	private final FeedLikeRepository feedLikeRepository;
	private final FeedRepository feedRepository;

	@Retryable(retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class,
			StaleObjectStateException.class}, maxAttempts = 15, backoff = @Backoff(delay = 30))
	public void feedLike(Long feedId, Long memberId) {
		Feed feed = feedRepository.findById(feedId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND));
		feed.like();
		feedLikeRepository.save(new FeedLike(feed, memberId));
	}

	@Retryable(retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class,
			StaleObjectStateException.class}, maxAttempts = 15, backoff = @Backoff(delay = 30))
	public void feedUnlike(Long feedId, Long memberId) {
		feedRepository.findById(feedId).orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND))
				.unlike();
		feedLikeRepository.deleteByFeedIdAndMemberId(feedId, memberId);
	}
}
