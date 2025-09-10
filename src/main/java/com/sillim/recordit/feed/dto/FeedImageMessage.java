package com.sillim.recordit.feed.dto;

public record FeedImageMessage(
		Long feedId, String fileName, String contentType, byte[] fileBytes) {}
