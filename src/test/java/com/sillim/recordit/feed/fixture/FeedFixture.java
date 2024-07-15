package com.sillim.recordit.feed.fixture;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.member.domain.Member;
import java.util.List;

public enum FeedFixture {
	DEFAULT("title", "content", List.of("https://image.url")),
	;
	private final String title;
	private final String content;
	private final List<String> feedImageUrls;

	FeedFixture(String title, String content, List<String> feedImageUrls) {
		this.title = title;
		this.content = content;
		this.feedImageUrls = feedImageUrls;
	}

	public Feed getFeed(Member member) {
		return new Feed(this.title, this.content, member);
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public List<String> getFeedImageUrls() {
		return feedImageUrls;
	}
}
