package com.sillim.recordit.feed.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedCommentTest {

    @Test
    @DisplayName("피드를 생성할 수 있다.")
    void createFeed() {
        Member member = MemberFixture.DEFAULT.getMember();
        Feed feed = FeedFixture.DEFAULT.getFeed(member);
        FeedComment feedComment = new FeedComment("content", feed, member);

        assertThat(feedComment.getContent()).isEqualTo("content");
    }
}