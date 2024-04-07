package com.sillim.recordit.member.service;

import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

	private final MemberRepository memberRepository;

	public Member findByOAuthAccount(String account) {
		return memberRepository
				.findByAuthOauthAccount(account)
				.orElseThrow(EntityNotFoundException::new);
	}

	public Member findByMemberId(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);
	}
}
