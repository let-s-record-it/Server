package com.sillim.recordit.invite.domain;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.domain.BaseTime;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(indexes = {@Index(name = "invite_code_idx", columnList = "inviteCode", unique = true)})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InviteLink extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invite_link_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private String inviteCode;

	@Column(nullable = false)
	private LocalDateTime expiredTime;

	@Column(nullable = false)
	private boolean expired;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	public InviteLink(
			String inviteCode, LocalDateTime expiredTime, boolean expired, Calendar calendar) {
		this.inviteCode = inviteCode;
		this.expiredTime = expiredTime;
		this.expired = expired;
		this.calendar = calendar;
	}

	public void expire() {
		this.expired = true;
	}
}
