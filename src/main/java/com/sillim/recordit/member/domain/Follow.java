package com.sillim.recordit.member.domain;

import com.sillim.recordit.global.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "follow_id", nullable = false)
	private Long id;

	private Long followerId;

	private Long followedId;

	public Follow(Member follower, Member followed) {
		this.followerId = follower.getId();
		this.followedId = followed.getId();
	}
}
