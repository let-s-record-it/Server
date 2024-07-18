package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.service.MemberQueryService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedCommandService {

	private final FeedRepository feedRepository;
	private final FeedImageUploadService feedImageUploadService;
	private final MemberQueryService memberQueryService;

	public Long addFeed(FeedAddRequest request, List<MultipartFile> images, Long memberId)
			throws IOException {
		List<String> imageUrls = new ArrayList<>();
		if (images != null) {
			imageUrls = feedImageUploadService.upload(images);
		}
		return feedRepository
				.save(request.toFeed(memberQueryService.findByMemberId(memberId), imageUrls))
				.getId();
	}

	public void removeFeed(Long feedId) {
		feedRepository
				.findById(feedId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND))
				.delete();
	}
}
