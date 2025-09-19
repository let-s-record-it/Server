package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedLike;
import com.sillim.recordit.feed.repository.FeedLikeRepository;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.service.MemberQueryService;
import com.sillim.recordit.pushalarm.dto.PushMessage;
import com.sillim.recordit.pushalarm.service.AlarmService;
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
	private final AlarmService alarmService;
	private final MemberQueryService memberQueryService;

	@Retryable(
			retryFor = {
				OptimisticLockException.class,
				ObjectOptimisticLockingFailureException.class,
				StaleObjectStateException.class
			},
			maxAttempts = 20,
			backoff = @Backoff(delay = 30))
	public void feedLike(Long feedId, Long memberId) {
		Feed feed =
				feedRepository
						.findById(feedId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND));
		feed.like();
		feedLikeRepository.save(new FeedLike(feed, memberId));

		if (!feed.isOwner(memberId)) {
			alarmService.pushAlarm(
					memberId,
					feed.getMemberId(),
					PushMessage.fromFeedLike(
							feed.getId(),
							memberQueryService.findByMemberId(memberId).getPersonalId(),
							feed.getTitle()));
		}
	}

	@Retryable(
			retryFor = {
				OptimisticLockException.class,
				ObjectOptimisticLockingFailureException.class,
				StaleObjectStateException.class
			},
			maxAttempts = 20,
			backoff = @Backoff(delay = 30))
	public void feedUnlike(Long feedId, Long memberId) {
		feedRepository
				.findById(feedId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND))
				.unlike();
		feedLikeRepository.deleteByFeedIdAndMemberId(feedId, memberId);
	}
}
