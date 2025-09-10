package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.FeedImageMessage;
import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.dto.request.FeedModifyRequest;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.gcp.service.ImageUploadService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.global.util.FileUtils;
import com.sillim.recordit.rabbitmq.dto.Message;
import com.sillim.recordit.rabbitmq.dto.MessageType;
import com.sillim.recordit.rabbitmq.service.MessagePublisher;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedCommandService {

	private final FeedRepository feedRepository;
	private final ImageUploadService imageUploadService;
	private final MessagePublisher messagePublisher;

	public Long addFeed(FeedAddRequest request, List<MultipartFile> images, Long memberId) {
		Long feedId = feedRepository.save(request.toFeed(memberId)).getId();

		messagePublisher.send(
				new Message<>(
						MessageType.IMAGES.name(),
						images.stream()
								.map(
										image -> {
											try {
												return new FeedImageMessage(
														feedId,
														generateImageName(image),
														image.getContentType(),
														image.getBytes());
											} catch (IOException e) {
												throw new RuntimeException(e);
											}
										}),
						MessageType.IMAGES));
		return feedId;
	}

	public void modifyFeed(
			FeedModifyRequest request, List<MultipartFile> newImages, Long feedId, Long memberId)
			throws IOException {
		Feed feed =
				feedRepository
						.findById(feedId)
						.orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND));
		feed.validateAuthenticatedUser(memberId);
		feed.modify(
				request.title(),
				request.content(),
				request.existingImageUrls(),
				imageUploadService.uploadImages(newImages));
	}

	public void removeFeed(Long feedId) {
		feedRepository
				.findById(feedId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND))
				.delete();
	}

	private static String generateImageName(MultipartFile image) {
		return FileUtils.generateUUIDFileName(Objects.requireNonNull(image.getOriginalFilename()));
	}
}
