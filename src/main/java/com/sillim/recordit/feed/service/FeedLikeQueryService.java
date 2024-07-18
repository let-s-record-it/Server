package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.repository.FeedLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedLikeQueryService {

    private final FeedLikeRepository feedLikeRepository;

    public boolean isLiked(Long feedId, Long memberId) {
        return feedLikeRepository.existsByFeedIdAndMemberId(feedId, memberId);
    }
}
