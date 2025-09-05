package com.sillim.recordit.member.domain;

import com.sillim.recordit.global.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member_device", indexes = @Index(name = "idx_identifier", columnList = "identifier"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDevice extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_device_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private String identifier;

	@Column(nullable = false)
	private String model;

	@Column(nullable = false)
	private String fcmToken;

	private Long memberId;

	@Builder
	public MemberDevice(String identifier, String model, String fcmToken, Member member) {
		this.identifier = identifier;
		this.model = model;
		this.fcmToken = fcmToken;
		this.memberId = member.getId();
	}
}
