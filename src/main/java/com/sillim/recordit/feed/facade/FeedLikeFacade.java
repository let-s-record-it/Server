package com.sillim.recordit.feed.facade;

import com.sillim.recordit.feed.service.FeedLikeService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedLikeFacade {
	private final FeedLikeService feedLikeService;

	public void feedLikeRetry(Long feedId, Long memberId, int maxRetry)
			throws InterruptedException {
		for (int attempt = 0; attempt < maxRetry; attempt++) {
			try {
				feedLikeService.feedLike(feedId, memberId);
				return;
			} catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
				Thread.sleep(30);
			}
		}
		throw new ApplicationException(ErrorCode.FAILED_TO_ACQUIRE_OPTIMISTIC_LOCK);
	}

	public void feedUnlikeRetry(Long feedId, Long memberId, int maxRetry)
			throws InterruptedException {
		for (int attempt = 0; attempt < maxRetry; attempt++) {
			try {
				feedLikeService.feedUnlike(feedId, memberId);
				return;
			} catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
				Thread.sleep(30);
			}
		}
		throw new ApplicationException(ErrorCode.FAILED_TO_ACQUIRE_OPTIMISTIC_LOCK);
	}
}
