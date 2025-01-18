package com.sillim.recordit.member.service;

import com.sillim.recordit.member.dto.request.ProfileModifyRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {

	private final MemberQueryService memberQueryService;

	public void modifyMemberInfo(ProfileModifyRequest request, Long memberId) {
		memberQueryService.findByMemberId(memberId).modifyInfo(request.name(), request.job());
	}
}
