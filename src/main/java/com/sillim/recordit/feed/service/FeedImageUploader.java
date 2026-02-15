package com.sillim.recordit.feed.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedImage;
import com.sillim.recordit.feed.dto.FeedImageMessage;
import com.sillim.recordit.feed.repository.FeedImageRepository;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedImageUploader {
	private static final String GCS_HOST = "https://storage.googleapis.com/";

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

	private final Storage storage;
	private final FeedRepository feedRepository;
	private final FeedImageRepository feedImageRepository;

	@Async
	@Retryable(
			retryFor = {ApplicationException.class},
			backoff = @Backoff(delay = 2000))
	public void uploadImages(List<FeedImageMessage> images) {
		for (FeedImageMessage image : images) {
			uploadImage(image);
		}
	}

	public void uploadImage(FeedImageMessage image) {
		try {
			storage.createFrom(
					BlobInfo.newBuilder(bucketName, image.fileName())
							.setContentType(image.contentType())
							.build(),
					new ByteArrayInputStream(image.fileBytes()));
		} catch (IOException e) {
			throw new ApplicationException(ErrorCode.IMAGE_UPLOAD_FAILED);
		}

		String imageUrl = GCS_HOST + bucketName + "/" + image.fileName();
		Feed feed =
				feedRepository
						.findById(image.feedId())
						.orElseThrow(() -> new ApplicationException(ErrorCode.FEED_NOT_FOUND));
		feedImageRepository.save(new FeedImage(imageUrl, feed));
	}
}
