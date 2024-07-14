package com.sillim.recordit.feed.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedImage;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.feed.InvalidFeedImageCountException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedImagesTest {

	@Test
	@DisplayName("피드 이미지 개수가 10개 이하면 저장된다.")
	void saveWhenFeedImageCount10OrUnder() {
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = FeedFixture.DEFAULT.getFeed(member);
		List<FeedImage> feedImageList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			feedImageList.add(new FeedImage("https://image.url", feed));
		}

		FeedImages feedImages = new FeedImages(feedImageList);
		assertAll(
				() -> {
					assertThat(feedImages.getFeedImageCount()).isEqualTo(10);
					assertThat(feedImages.getFeedImages().get(0).getImageUrl())
							.isEqualTo("https://image.url");
				});
	}

	@Test
	@DisplayName("피드 이미지 개수가 11개 이상이면 InvalidFeedImageCountExceptoin이 발생한다.")
	void throwInvalidFeedImageCountExceptionIfFeedImageCountExceed10() {
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = FeedFixture.DEFAULT.getFeed(member);
		List<FeedImage> feedImageList = new ArrayList<>();
		for (int i = 0; i < 11; i++) {
			feedImageList.add(new FeedImage("https://image.url", feed));
		}

		assertThatCode(() -> new FeedImages(feedImageList))
				.isInstanceOf(InvalidFeedImageCountException.class)
				.hasMessage(ErrorCode.OVER_FEED_IMAGE_COUNT.getDescription());
	}
}
