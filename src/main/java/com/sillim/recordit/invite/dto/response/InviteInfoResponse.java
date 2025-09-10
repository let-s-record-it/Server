package com.sillim.recordit.invite.dto.response;

public record InviteInfoResponse(
		Long calendarId, String calendarTitle, Long ownerId, String ownerName) {}
