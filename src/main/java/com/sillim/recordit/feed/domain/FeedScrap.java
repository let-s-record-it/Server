package com.sillim.recordit.feed.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
		uniqueConstraints =
				@UniqueConstraint(
						name = "feedScrapMember",
						columnNames = {"feed_id", "member_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedScrap {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, name = "feed_scrap_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feed_id")
	private Feed feed;

	@Column(name = "member_id")
	private Long memberId;

	public FeedScrap(Feed feed, Long memberId) {
		this.feed = feed;
		this.memberId = memberId;
	}
}
