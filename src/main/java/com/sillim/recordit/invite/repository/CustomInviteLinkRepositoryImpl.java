package com.sillim.recordit.invite.repository;

import static com.sillim.recordit.invite.domain.QInviteLink.inviteLink;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.invite.domain.InviteLink;
import com.sillim.recordit.invite.dto.response.InviteInfoResponse;
import com.sillim.recordit.member.domain.Member;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomInviteLinkRepositoryImpl extends QuerydslRepositorySupport
		implements CustomInviteLinkRepository {

	public CustomInviteLinkRepositoryImpl() {
		super(InviteLink.class);
	}

	@Override
	public InviteInfoResponse findInfoByInviteCode(String inviteCode) {
		InviteLink result =
				selectFrom(inviteLink)
						.leftJoin(inviteLink.calendar)
						.fetchJoin()
						.leftJoin(inviteLink.calendar.member)
						.fetchJoin()
						.where(inviteLink.inviteCode.eq(inviteCode))
						.fetchOne();
		Calendar calendar = result.getCalendar();
		Member member = calendar.getMember();
		return new InviteInfoResponse(
				calendar.getId(), calendar.getTitle(), member.getId(), member.getName());
	}
}
