package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedImage;
import com.sillim.recordit.feed.dto.FeedImageMessage;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedImageRepository;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FeedImageUploaderTest {

	@Mock Storage storage;
	@Mock FeedRepository feedRepository;
	@Mock FeedImageRepository feedImageRepository;
	@InjectMocks FeedImageUploader feedImageUploader;

	@Test
	@DisplayName("이미지를 GCS에 업로드하고 FeedImage를 저장한다.")
	void uploadImage() throws IOException {
		Long feedId = 1L;
		Long memberId = 1L;
		Feed feed = FeedFixture.DEFAULT.getFeed(memberId);
		FeedImageMessage imageMessage =
				new FeedImageMessage(
						feedId,
						"test-image.jpg",
						"image/jpeg",
						"test-content".getBytes(StandardCharsets.UTF_8));
		ReflectionTestUtils.setField(feedImageUploader, "bucketName", "test-bucket");
		given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));

		feedImageUploader.uploadImage(imageMessage);

		then(storage).should(times(1)).createFrom(any(BlobInfo.class), any(InputStream.class));
		then(feedImageRepository).should(times(1)).save(any(FeedImage.class));
	}

	@Test
	@DisplayName("여러 이미지를 업로드할 수 있다.")
	void uploadImages() throws IOException {
		Long feedId = 1L;
		Long memberId = 1L;
		Feed feed = FeedFixture.DEFAULT.getFeed(memberId);
		List<FeedImageMessage> imageMessages =
				List.of(
						new FeedImageMessage(
								feedId,
								"image1.jpg",
								"image/jpeg",
								"content1".getBytes(StandardCharsets.UTF_8)),
						new FeedImageMessage(
								feedId,
								"image2.jpg",
								"image/jpeg",
								"content2".getBytes(StandardCharsets.UTF_8)),
						new FeedImageMessage(
								feedId,
								"image3.jpg",
								"image/jpeg",
								"content3".getBytes(StandardCharsets.UTF_8)));
		ReflectionTestUtils.setField(feedImageUploader, "bucketName", "test-bucket");
		given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));

		feedImageUploader.uploadImages(imageMessages);

		then(storage).should(times(3)).createFrom(any(BlobInfo.class), any(InputStream.class));
		then(feedImageRepository).should(times(3)).save(any(FeedImage.class));
	}

	@Test
	@DisplayName("존재하지 않는 피드에 이미지를 업로드하면 예외가 발생한다.")
	void uploadImageWithNonExistentFeed() {
		Long feedId = 999L;
		FeedImageMessage imageMessage =
				new FeedImageMessage(
						feedId,
						"test-image.jpg",
						"image/jpeg",
						"test-content".getBytes(StandardCharsets.UTF_8));
		ReflectionTestUtils.setField(feedImageUploader, "bucketName", "test-bucket");
		given(feedRepository.findById(feedId)).willReturn(Optional.empty());

		assertThatThrownBy(() -> feedImageUploader.uploadImage(imageMessage))
				.isInstanceOf(ApplicationException.class)
				.hasMessage(ErrorCode.FEED_NOT_FOUND.getDescription());
	}

	@Test
	@DisplayName("Storage에서 IOException 발생 시 예외가 전파된다.")
	void uploadImageThrowsIOException() throws IOException {
		Long feedId = 1L;
		FeedImageMessage imageMessage =
				new FeedImageMessage(
						feedId,
						"test-image.jpg",
						"image/jpeg",
						"test-content".getBytes(StandardCharsets.UTF_8));
		ReflectionTestUtils.setField(feedImageUploader, "bucketName", "test-bucket");
		given(storage.createFrom(any(BlobInfo.class), any(InputStream.class)))
				.willThrow(new IOException("GCS connection failed"));

		assertThatThrownBy(() -> feedImageUploader.uploadImage(imageMessage))
				.isInstanceOf(ApplicationException.class)
				.hasMessage(ErrorCode.IMAGE_UPLOAD_FAILED.getDescription());
	}
}
