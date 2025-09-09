package com.sillim.recordit.member.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.response.ProfileResponse;
import com.sillim.recordit.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

	private final MemberRepository memberRepository;

	public Member findByMemberId(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public ProfileResponse searchProfileByMemberId(Long memberId, Long myId) {
		Member me = findByMemberId(myId);
		Member other = findByMemberId(memberId);
		boolean isFollowing = existsFollower(me.getPersonalId(), other.getPersonalId());
		return ProfileResponse.of(other, isFollowing);
	}

	public Optional<Member> searchByAccount(String account) {
		return memberRepository.findByOauthAccount(account);
	}

	public List<Member> searchByPersonalIdPrefix(String personalIdPrefix) {
		return memberRepository.findByPersonalIdStartingWith(personalIdPrefix);
	}

	public boolean existsFollower(String myPersonalId, String otherPersonalId) {
		return memberRepository.existsByFollower(myPersonalId, otherPersonalId);
	}
}
