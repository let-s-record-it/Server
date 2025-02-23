package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.dto.request.FeedModifyRequest;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.gcp.service.ImageUploadService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.service.MemberQueryService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedCommandService {

	private final FeedRepository feedRepository;
	private final ImageUploadService imageUploadService;
	private final MemberQueryService memberQueryService;

	public Long addFeed(FeedAddRequest request, List<MultipartFile> images, Long memberId)
			throws IOException {
		return feedRepository
				.save(
						request.toFeed(
								memberQueryService.findByMemberId(memberId),
								imageUploadService.uploadImages(images)))
				.getId();
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
}
