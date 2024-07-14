package com.sillim.recordit.feed.dto.request;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.member.domain.Member;
import java.util.List;
import org.hibernate.validator.constraints.Length;

public record FeedAddRequest(
		@Length(min = 1, max = 30) String title,
		@Length(max = 5000) String content,
		@Length(max = 10) List<String> feedImageUrls) {

	public Feed toFeed(Member member) {
		return new Feed(title, content, member, feedImageUrls);
	}
}
