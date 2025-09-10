package com.sillim.recordit.feed.dto.request;

import org.hibernate.validator.constraints.Length;

public record FeedCommentAddRequest(@Length(max = 1000) String content) {}
