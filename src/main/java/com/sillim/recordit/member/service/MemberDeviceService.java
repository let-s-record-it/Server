package com.sillim.recordit.member.service;

import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberDevice;
import com.sillim.recordit.member.repository.MemberDeviceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberDeviceService {

	private final MemberDeviceRepository memberDeviceRepository;

	public void addMemberDeviceIfNotExists(
			String id, String model, String fcmToken, Member member) {
		if (!memberDeviceRepository.existsByIdentifier(id)) {
			memberDeviceRepository.save(
					MemberDevice.builder()
							.identifier(id)
							.model(model)
							.fcmToken(fcmToken)
							.member(member)
							.build());
		}
	}

	@Transactional(readOnly = true)
	public List<String> searchFcmTokensByMemberId(Long memberId) {
		return memberDeviceRepository.findFcmTokensByMemberId(memberId);
	}
}
