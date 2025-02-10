package com.sillim.recordit.enums.color;

import lombok.Getter;

@Getter
public enum DefaultColor {
	RED("e4341b", "빨간색"),
	ORANGE("f88849", "주황색"),
	YELLOW("ffdb12", "노란색"),
	GREEN("3dff1e", "초록색"),
	BLUE("1ea0ff", "파란색"),
	NAVY("370eab", "남색"),
	PURPLE("9600de", "보라색"),
	;
	private final String colorHex;
	private final String name;

	DefaultColor(String colorHex, String name) {
		this.colorHex = colorHex;
		this.name = name;
	}
}
