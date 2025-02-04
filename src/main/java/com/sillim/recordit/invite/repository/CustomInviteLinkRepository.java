package com.sillim.recordit.invite.repository;

import com.sillim.recordit.invite.dto.response.InviteInfoResponse;

public interface CustomInviteLinkRepository {

	InviteInfoResponse findInfoByInviteCode(String inviteCode);
}
