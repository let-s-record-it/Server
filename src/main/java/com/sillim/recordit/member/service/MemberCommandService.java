package com.sillim.recordit.member.service;

import com.sillim.recordit.gcp.service.ImageUploadService;
import com.sillim.recordit.member.dto.request.ProfileModifyRequest;
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

	public void modifyMemberInfo(ProfileModifyRequest request, Long memberId) {
		memberQueryService.findByMemberId(memberId).modifyInfo(request.name(), request.job());
	}

	public void modifyMemberProfileImage(MultipartFile newImage, Long memberId) throws IOException {
		memberQueryService
				.findByMemberId(memberId)
				.modifyProfileImageUrl(imageUploadService.uploadImage(newImage));
	}
}
