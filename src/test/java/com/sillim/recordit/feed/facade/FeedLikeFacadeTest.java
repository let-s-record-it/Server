package com.sillim.recordit.feed.facade;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willThrow;

import com.sillim.recordit.feed.service.FeedLikeService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@ExtendWith(MockitoExtension.class)
class FeedLikeFacadeTest {

	@Mock FeedLikeService feedLikeService;
	@InjectMocks FeedLikeFacade feedLikeFacade;

	@Test
	@DisplayName("좋아요에 성공하면 예외가 발생하지 않는다.")
	void feedLikeRetry() {
		long feedId = 1L;
		long memberId = 1L;
		int maxRetry = 20;

		assertThatCode(() -> feedLikeFacade.feedLikeRetry(feedId, memberId, maxRetry))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("좋아요에 실패하면 ApplicationException이 발생한다.")
	void throwApplicationExceptionIfFailedToLike() {
		long feedId = 1L;
		long memberId = 1L;
		int maxRetry = 20;

		willThrow(ObjectOptimisticLockingFailureException.class)
				.given(feedLikeService)
				.feedLike(anyLong(), anyLong());
		assertThatCode(() -> feedLikeFacade.feedLikeRetry(feedId, memberId, maxRetry))
				.isInstanceOf(ApplicationException.class)
				.hasMessage(ErrorCode.FAILED_TO_ACQUIRE_OPTIMISTIC_LOCK.getDescription());
	}

	@Test
	@DisplayName("좋아요 취소에 성공하면 예외가 발생하지 않는다.")
	void feedUnlikeRetry() {
		long feedId = 1L;
		long memberId = 1L;
		int maxRetry = 20;

		assertThatCode(() -> feedLikeFacade.feedUnlikeRetry(feedId, memberId, maxRetry))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("좋아요 취소에 실패하면 ApplicationException이 발생한다.")
	void throwApplicationExceptionIfFailedToUnlike() {
		long feedId = 1L;
		long memberId = 1L;
		int maxRetry = 20;

		willThrow(ObjectOptimisticLockingFailureException.class)
				.given(feedLikeService)
				.feedUnlike(anyLong(), anyLong());
		assertThatCode(() -> feedLikeFacade.feedUnlikeRetry(feedId, memberId, maxRetry))
				.isInstanceOf(ApplicationException.class)
				.hasMessage(ErrorCode.FAILED_TO_ACQUIRE_OPTIMISTIC_LOCK.getDescription());
	}
}
