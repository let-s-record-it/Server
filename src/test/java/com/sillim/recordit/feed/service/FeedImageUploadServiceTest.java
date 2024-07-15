package com.sillim.recordit.feed.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.amazonaws.services.s3.AmazonS3;
import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FeedImageUploadServiceTest {

	FeedQueryService feedQueryService;
	AmazonS3 amazonS3;
	FeedImageUploadService feedImageUploadService;

	@BeforeEach
	void initObjects() {
		this.feedQueryService = mock(FeedQueryService.class);
		this.amazonS3 = mock(AmazonS3.class);
		this.feedImageUploadService =
				new FeedImageUploadService(feedQueryService, amazonS3, "test");
	}

	@Test
	@DisplayName("피드 이미지를 업로드할 수 있다.")
	void uploadFeedImages() throws IOException {
		long feedId = 1L;
		MockMultipartFile multipartFile =
				new MockMultipartFile(
						"images",
						"image.jpg",
						"text/plain",
						"test1".getBytes(StandardCharsets.UTF_8));
		Feed feed = mock(Feed.class);
		given(feedQueryService.searchById(eq(feedId))).willReturn(feed);
		given(amazonS3.getUrl(eq("test"), eq("image.jpg")))
				.willReturn(new URL("http", "image.url", "image"));

		feedImageUploadService.upload(List.of(multipartFile), feedId);

		then(feed).should(times(1)).setFeedImages(anyList());
	}

	@Test
	@DisplayName("피드 이미지가 비어있으면 FileNotFoundException이 발생한다.")
	void throwFileNotFoundExceptionIfFeedImageFileNotExists() throws IOException {
		long feedId = 1L;
		MockMultipartFile multipartFile =
				new MockMultipartFile(
						"images", "image.jpg", "text/plain", "".getBytes(StandardCharsets.UTF_8));
		Feed feed = mock(Feed.class);
		given(feedQueryService.searchById(eq(feedId))).willReturn(feed);

		assertThatCode(() -> feedImageUploadService.upload(List.of(multipartFile), feedId))
				.isInstanceOf(FileNotFoundException.class)
				.hasMessage(ErrorCode.FILE_NOT_FOUND.getDescription());
	}
}
