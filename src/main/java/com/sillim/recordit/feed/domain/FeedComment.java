package com.sillim.recordit.feed.domain;

import com.sillim.recordit.feed.domain.vo.FeedCommentContent;
import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Entity
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedComment extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, name = "feed_comment_id")
	private Long id;

	private FeedCommentContent content;

	@Column(nullable = false)
	@ColumnDefault("false")
	private Boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feed_id")
	private Feed feed;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	public FeedComment(String content, Feed feed, Long memberId) {
		this.content = new FeedCommentContent(content);
		this.deleted = false;
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

	public void delete() {
		this.deleted = true;
	}
}
