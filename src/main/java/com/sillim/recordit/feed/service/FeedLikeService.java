package com.sillim.recordit.feed.service;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedLike;
import com.sillim.recordit.feed.repository.FeedLikeRepository;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedLikeService {

    private final FeedLikeRepository feedLikeRepository;
    private final FeedRepository feedRepository;
    private final MemberQueryService memberQueryService;

    public void feedLike(Long feedId, Long memberId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND));
        feed.like();
        feedLikeRepository.save(new FeedLike(feed, memberQueryService.findByMemberId(memberId)));
    }

    public void feedUnlike(Long feedId, Long memberId) {
        feedRepository.findById(feedId).orElseThrow(() -> new RecordNotFoundException(ErrorCode.FEED_NOT_FOUND)).unlike();
        feedLikeRepository.deleteByFeedIdAndMemberId(feedId, memberId);
    }

}
