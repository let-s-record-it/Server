package com.sillim.recordit.feed.domain;

import com.sillim.recordit.feed.domain.vo.FeedCommentContent;
import com.sillim.recordit.global.domain.BaseEntity;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Entity
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedComment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, name = "feed_comment_id")
	private Long id;

	private FeedCommentContent content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "feed_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Feed feed;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	public FeedComment(String content, Feed feed, Long memberId) {
		this.content = new FeedCommentContent(content);
		this.feed = feed;
		this.memberId = memberId;
	}

	public String getContent() {
		return content.getContent();
	}

	public boolean isOwner(Long memberId) {
		return Objects.equals(this.memberId, memberId);
	}

	public void validateAuthenticatedUser(Long memberId) {
		if (!isOwner(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}
	}
}
