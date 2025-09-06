package com.sillim.recordit.member.dto.response;

/**
 *
 * @param weightRepPersonalId: 추천 후보를 팔로우하는 대표 Member Id
 * @param weightRepName: 추천 후보를 팔로우하는 대표 Member Name
 * @param weightCount: 추천 후보를 팔로우하는 Member 수
 * @param commonFollowCount: 추천 후보와 공통으로 팔로우하는 수
 */
public record FollowRecommendResponse(
		Long internalId,
		String personalId,
		String name,
		String profileImageUrl,
		String weightRepPersonalId,
		String weightRepName,
		Long weightCount,
		Long commonFollowCount) {}
