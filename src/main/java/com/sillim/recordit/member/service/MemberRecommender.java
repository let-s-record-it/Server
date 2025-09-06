package com.sillim.recordit.member.service;

import com.sillim.recordit.member.dto.response.FollowRecommendResponse;
import com.sillim.recordit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberRecommender {

    private final MemberRepository memberRepository;

    public List<FollowRecommendResponse> recommendFollows(String personalId) {
        return memberRepository.recommendFriendOfFriends(personalId);
    }
}
