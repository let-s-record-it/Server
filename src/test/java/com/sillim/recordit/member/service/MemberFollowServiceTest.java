package com.sillim.recordit.member.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.repository.MemberRepository;
import com.sillim.recordit.pushalarm.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberFollowServiceTest {

    @Mock MemberQueryService memberQueryService;
    @Mock MemberRepository memberRepository;
    @Mock AlarmService alarmService;
    @InjectMocks MemberFollowService memberFollowService;

    @Test
    @DisplayName("자기 자신을 팔로우하면 InvalidRequestException이 발생한다.")
    void throwInvalidRequestExceptionIfFollowMySelf() {
        long memberId = 1L;
        Member follower = mock(Member.class);
        given(memberQueryService.findByMemberId(eq(memberId))).willReturn(follower);
        given(follower.equalsId(eq(memberId))).willReturn(true);

        assertThatCode(() -> memberFollowService.follow(memberId, memberId))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
    }

    @Test
    @DisplayName("멤버를 팔로우한다.")
    void followMember() throws IOException {
        long followerId = 1L;
        long followedId = 2L;
        Member follower = mock(Member.class);
        Member followed = mock(Member.class);
        given(memberQueryService.findByMemberId(eq(followerId))).willReturn(follower);
        given(memberQueryService.findByMemberId(eq(followedId))).willReturn(followed);

        memberFollowService.follow(followerId, followedId);

        verify(follower, times(1)).follow(followed);
        verify(memberRepository, times(1)).save(follower);
    }
}