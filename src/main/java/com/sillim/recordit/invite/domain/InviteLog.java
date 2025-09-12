package com.sillim.recordit.invite.domain;

import com.sillim.recordit.global.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InviteLog extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invite_log_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private Long inviterId;

	@Column(nullable = false)
	private Long invitedId;

	@Column(nullable = false)
	private Long calendarId;

	@Column(nullable = false)
	private InviteState state;

	public InviteLog(Long inviterId, Long invitedId, Long calendarId, InviteState state) {
		this.inviterId = inviterId;
		this.invitedId = invitedId;
		this.calendarId = calendarId;
		this.state = state;
	}

	public void accept() {
		this.state = InviteState.ACCEPT;
	}

	public void reject() {
		this.state = InviteState.REJECT;
	}

	public boolean isInvitedMember(Long memberId) {
		return this.invitedId.equals(memberId);
	}
}
