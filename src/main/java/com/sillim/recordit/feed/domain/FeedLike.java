package com.sillim.recordit.feed.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "feedLikeMember", columnNames = {"feed_id", "member_id"}))
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, name = "feed_like_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feed_id")
	private Feed feed;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	public FeedLike(Feed feed, Long memberId) {
		this.feed = feed;
		this.memberId = memberId;
	}
}
