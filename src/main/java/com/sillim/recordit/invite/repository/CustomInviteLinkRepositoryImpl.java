package com.sillim.recordit.invite.repository;

import static com.sillim.recordit.invite.domain.QInviteLink.inviteLink;

import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.invite.domain.InviteLink;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomInviteLinkRepositoryImpl extends QuerydslRepositorySupport
		implements CustomInviteLinkRepository {

	public CustomInviteLinkRepositoryImpl() {
		super(InviteLink.class);
	}

	@Override
	public InviteLink findInfoByInviteCode(String inviteCode) {
		return selectFrom(inviteLink)
				.leftJoin(inviteLink.calendar)
				.fetchJoin()
				.leftJoin(inviteLink.calendar.member)
				.fetchJoin()
				.where(inviteLink.inviteCode.eq(inviteCode))
				.fetchOne();
	}
}
