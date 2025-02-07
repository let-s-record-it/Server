package com.sillim.recordit.invite.repository;

import com.sillim.recordit.invite.domain.InviteLink;

public interface CustomInviteLinkRepository {

	InviteLink findInfoByInviteCode(String inviteCode);
}
