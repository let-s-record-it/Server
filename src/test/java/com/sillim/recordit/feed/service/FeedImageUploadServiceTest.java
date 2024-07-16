package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.amazonaws.services.s3.AmazonS3;
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

	AmazonS3 amazonS3;
	FeedImageUploadService feedImageUploadService;

	@BeforeEach
	void initObjects() {
		this.amazonS3 = mock(AmazonS3.class);
		this.feedImageUploadService = new FeedImageUploadService(amazonS3, "test");
	}

	@Test
	@DisplayName("피드 이미지를 업로드할 수 있다.")
	void uploadFeedImages() throws IOException {
		MockMultipartFile multipartFile =
				new MockMultipartFile(
						"images",
						"image.jpg",
						"text/plain",
						"test1".getBytes(StandardCharsets.UTF_8));
		given(amazonS3.getUrl(eq("test"), eq("image.jpg")))
				.willReturn(new URL("http", "image.url", "/images/1"));

		List<String> imageUrls = feedImageUploadService.upload(List.of(multipartFile));

		assertThat(imageUrls).hasSize(1);
		assertThat(imageUrls.get(0)).isEqualTo("http://image.url/images/1");
	}

	@Test
	@DisplayName("피드 이미지가 비어있으면 FileNotFoundException이 발생한다.")
	void throwFileNotFoundExceptionIfFeedImageFileNotExists() {
		long feedId = 1L;
		MockMultipartFile multipartFile =
				new MockMultipartFile(
						"images", "image.jpg", "text/plain", "".getBytes(StandardCharsets.UTF_8));

		assertThatCode(() -> feedImageUploadService.upload(List.of(multipartFile)))
				.isInstanceOf(FileNotFoundException.class)
				.hasMessage(ErrorCode.FILE_NOT_FOUND.getDescription());
	}
}
