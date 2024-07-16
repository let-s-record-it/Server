package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.repository.FeedRepository;
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
	private final FeedImageUploadService feedImageUploadService;
	private final MemberQueryService memberQueryService;

	public Long addFeed(FeedAddRequest request, List<MultipartFile> images, Long memberId)
			throws IOException {
		Feed feed =
				feedRepository.save(
						request.toFeed(
								memberQueryService.findByMemberId(memberId),
								feedImageUploadService.upload(images)));
		return feed.getId();
	}
}
