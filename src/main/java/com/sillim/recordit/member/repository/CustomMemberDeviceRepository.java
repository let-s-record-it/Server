package com.sillim.recordit.member.repository;

import java.util.List;

public interface CustomMemberDeviceRepository {
	List<String> findFcmTokensByMemberId(Long memberId);
}
