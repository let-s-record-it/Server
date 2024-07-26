package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedComment;
import com.sillim.recordit.feed.dto.request.FeedCommentAddRequest;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedCommentRepository;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedCommentCommandServiceTest {

    @Mock
    FeedCommentRepository feedCommentRepository;
    @Mock
    FeedRepository feedRepository;
    @Mock
    MemberQueryService memberQueryService;
    @InjectMocks
    FeedCommentCommandService feedCommentCommandService;

    @Test
    @DisplayName("피드 댓글을 추가할 수 있다.")
    void addFeedComment() {
        FeedCommentAddRequest request = new FeedCommentAddRequest("content");
        long feedId = 1L;
        long feedCommentId = 1L;
        long memberId = 1L;
        Feed feed = FeedFixture.DEFAULT.getFeed(MemberFixture.DEFAULT.getMember());
        Member commentWriter = MemberFixture.DEFAULT.getMember();
        FeedComment feedComment = spy(new FeedComment("content", feed, commentWriter));
        given(feedComment.getId()).willReturn(feedCommentId);
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));
        given(memberQueryService.findByMemberId(feedId)).willReturn(commentWriter);
        given(feedCommentRepository.save(any(FeedComment.class))).willReturn(feedComment);

        Long savedFeedCommentId = feedCommentCommandService.addFeedComment(request, feedId,
                memberId);

        assertThat(savedFeedCommentId).isEqualTo(feedCommentId);
    }
}