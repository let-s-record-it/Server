package com.sillim.recordit.member.domain;

public enum TokenType {
	BEARER("Bearer");

	private final String value;

	TokenType(String value) {
		this.value = value;
	}

	public String getValueWithSpace() {
		return this.value + " ";
	}
}
