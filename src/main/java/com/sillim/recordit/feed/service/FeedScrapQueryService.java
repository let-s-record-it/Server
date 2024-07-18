package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.repository.FeedScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedScrapQueryService {

    private final FeedScrapRepository feedScrapRepository;

    public boolean isScraped(Long feedId, Long memberId) {
        return feedScrapRepository.existsByFeedIdAndMemberId(feedId, memberId);
    }
}
