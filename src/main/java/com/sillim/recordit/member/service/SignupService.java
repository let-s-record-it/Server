package com.sillim.recordit.member.service;

import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.request.SignupRequest;
import com.sillim.recordit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

	public static final String BEARER = "Bearer ";

	private final MemberRepository memberRepository;

	@Transactional
	public Member signup(SignupRequest signupRequest) {
		return memberRepository.save(signupRequest.toMember());
	}
}