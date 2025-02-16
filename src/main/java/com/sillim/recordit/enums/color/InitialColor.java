package com.sillim.recordit.enums.color;

import lombok.Getter;

@Getter
public enum InitialColor {
	NONE("40d974", "기본", true),
	RED("e4341b", "빨간색", false),
	ORANGE("f88849", "주황색", false),
	YELLOW("ffdb12", "노란색", false),
	GREEN("3dff1e", "초록색", false),
	BLUE("1ea0ff", "파란색", false),
	NAVY("370eab", "남색", false),
	PURPLE("9600de", "보라색", false),
	;
	private final String colorHex;
	private final String name;
	private final boolean isDefault;

	InitialColor(String colorHex, String name, boolean isDefault) {
		this.colorHex = colorHex;
		this.name = name;
		this.isDefault = isDefault;
	}
}
