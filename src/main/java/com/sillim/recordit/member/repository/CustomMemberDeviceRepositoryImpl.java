package com.sillim.recordit.member.repository;

import static com.sillim.recordit.member.domain.QMemberDevice.memberDevice;

import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.member.domain.MemberDevice;
import java.util.List;

public class CustomMemberDeviceRepositoryImpl extends QuerydslRepositorySupport
		implements CustomMemberDeviceRepository {

	public CustomMemberDeviceRepositoryImpl() {
		super(MemberDevice.class);
	}

	@Override
	public List<String> findFcmTokensByMemberId(Long memberId) {
		return select(memberDevice.fcmToken)
				.from(memberDevice)
				.where(memberDevice.deleted.isFalse(), memberDevice.memberId.eq(memberId))
				.fetch();
	}

	@Override
	public void updateFcmToken(String deviceId, String fcmToken, Long memberId) {
		update(memberDevice)
				.set(memberDevice.fcmToken, fcmToken)
				.where(
						memberDevice.deleted.isFalse(),
						memberDevice.memberId.eq(memberId),
						memberDevice.identifier.eq(deviceId))
				.execute();
	}
}
