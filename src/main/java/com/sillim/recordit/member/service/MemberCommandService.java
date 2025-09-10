package com.sillim.recordit.member.service;

import com.sillim.recordit.gcp.service.ImageUploadService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.request.ProfileModifyRequest;
import com.sillim.recordit.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {

	private final MemberQueryService memberQueryService;
	private final ImageUploadService imageUploadService;
	private final MemberRepository memberRepository;

	public void modifyMemberInfo(ProfileModifyRequest request, Long memberId) {
		Member member = memberQueryService.findByMemberId(memberId);
		member.modifyInfo(request.name(), request.job());
		memberRepository.save(member);
	}

	public void modifyMemberProfileImage(MultipartFile newImage, Long memberId) throws IOException {
		memberQueryService
				.findByMemberId(memberId)
				.modifyProfileImageUrl(imageUploadService.uploadImage(newImage));
	}

	public void withdrawMember(Long memberId) {
		memberQueryService.findByMemberId(memberId).delete();
	}
}
