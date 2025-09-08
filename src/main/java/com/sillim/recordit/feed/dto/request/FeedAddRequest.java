package com.sillim.recordit.feed.dto.request;

import com.sillim.recordit.feed.domain.Feed;
import org.hibernate.validator.constraints.Length;

public record FeedAddRequest(@Length(min = 1, max = 30) String title, @Length(max = 5000) String content) {

	public Feed toFeed(Long memberId) {
		return new Feed(title, content, memberId);
	}
}
