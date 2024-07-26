package com.sillim.recordit.feed.dto.request;

import java.util.List;
import org.hibernate.validator.constraints.Length;

public record FeedModifyRequest(
		@Length(min = 1, max = 30) String title,
		@Length(max = 5000) String content,
		List<String> existingImageUrls) {}
