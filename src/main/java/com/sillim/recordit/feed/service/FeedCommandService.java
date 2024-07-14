package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.member.service.MemberQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedCommandService {

	private final FeedRepository feedRepository;
	private final MemberQueryService memberQueryService;

	public Long addFeed(FeedAddRequest request, Long memberId) {
		return feedRepository
				.save(request.toFeed(memberQueryService.findByMemberId(memberId)))
				.getId();
	}
}
