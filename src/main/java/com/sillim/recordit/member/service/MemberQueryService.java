package com.sillim.recordit.member.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.repository.MemberRepository;
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
		return memberRepository
				.findById(memberId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public Optional<Member> searchByAccount(String account) {
		return memberRepository.findByOauthAccount(account);
	}
}
