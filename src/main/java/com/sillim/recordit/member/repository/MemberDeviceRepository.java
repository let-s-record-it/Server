package com.sillim.recordit.member.repository;

import com.sillim.recordit.member.domain.MemberDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDeviceRepository extends JpaRepository<MemberDevice, Long>, CustomMemberDeviceRepository {

	boolean existsByIdentifierAndMemberId(String identifier, Long memberId);

	void deleteByMemberId(Long memberId);
}
