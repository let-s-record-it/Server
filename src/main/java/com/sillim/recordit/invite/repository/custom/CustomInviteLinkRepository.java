package com.sillim.recordit.invite.repository.custom;

import com.sillim.recordit.invite.domain.InviteLink;

public interface CustomInviteLinkRepository {

	InviteLink findInfoByInviteCode(String inviteCode);
}
