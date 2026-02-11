package com.sillim.recordit.member.service;

import com.sillim.recordit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberDeleteService {

	private final MemberRepository memberRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void hardDeleteMember(Long memberId) {
		memberRepository.deleteById(memberId);
	}
}
