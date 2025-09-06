package com.sillim.recordit.feed.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedImage;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.feed.InvalidFeedImageCountException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedImagesTest {

	@Test
	@DisplayName("피드 이미지 개수가 10개 이하면 저장된다.")
	void saveWhenFeedImageCount10OrUnder() {
		long memberId = 1L;
		Feed feed = FeedFixture.DEFAULT.getFeed(memberId);
		List<FeedImage> feedImageList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			feedImageList.add(new FeedImage("https://image.url", feed));
		}

		FeedImages feedImages = new FeedImages();
		feedImages.setFeedImages(feedImageList);
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
		long memberId = 1L;
		Feed feed = FeedFixture.DEFAULT.getFeed(memberId);
		List<FeedImage> feedImageList = new ArrayList<>();
		for (int i = 0; i < 11; i++) {
			feedImageList.add(new FeedImage("https://image.url", feed));
		}

		assertThatCode(() -> new FeedImages().setFeedImages(feedImageList))
				.isInstanceOf(InvalidFeedImageCountException.class)
				.hasMessage(ErrorCode.OVER_FEED_IMAGE_COUNT.getDescription());
	}

	@Test
	@DisplayName("이미 존재하는 이미지를 제외하고 다 삭제 후 새 이미지를 저장한다.")
	void removeExcludeExistingImageAndSaveNewIamges() {
		long memberId = 1L;
		Feed feed = FeedFixture.DEFAULT.getFeed(memberId);

		FeedImages feedImages =
				new FeedImages(
						new ArrayList<>(
								List.of(new FeedImage("url1", feed), new FeedImage("url2", feed))));

		feedImages.modifyFeedImages(List.of("url2"), List.of(new FeedImage("url3", feed)));

		List<String> feedImageUrls =
				feedImages.getFeedImages().stream().map(FeedImage::getImageUrl).toList();

		assertThat(feedImageUrls).hasSize(2);
		assertThat(feedImageUrls).containsExactly("url2", "url3");
	}
}
