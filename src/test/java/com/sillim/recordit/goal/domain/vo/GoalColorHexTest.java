package com.sillim.recordit.goal.domain.vo;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidColorHexException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GoalColorHexTest {

	@Test
	@DisplayName("GoalColorHex를 생성한다.")
	void newGoalColorHexTest() {

		assertThatCode(() -> new GoalColorHex("12aaAA34")).doesNotThrowAnyException();
		assertThatCode(() -> new GoalColorHex("aaAA34")).doesNotThrowAnyException();
		assertThatCode(() -> new GoalColorHex("1aA")).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("colorHex 문자열이 null이 아니어야 한다.")
	void validateNullTest() {

		assertThatThrownBy(() -> new GoalColorHex(null))
				.isInstanceOf(InvalidColorHexException.class)
				.hasMessage(ErrorCode.NULL_GOAL_COLOR_HEX.getDescription());
	}

	@Test
	@DisplayName("colorHex 문자열의 길이는 3, 6, 8이어야 한다.")
	void validateInvalidLengthTest() {

		// 3의 경계 2, 4
		assertThatThrownBy(() -> new GoalColorHex("11"))
				.isInstanceOf(InvalidColorHexException.class)
				.hasMessage(ErrorCode.INVALID_GOAL_COLOR_HEX.getDescription());

		assertThatThrownBy(() -> new GoalColorHex("1111"))
				.isInstanceOf(InvalidColorHexException.class)
				.hasMessage(ErrorCode.INVALID_GOAL_COLOR_HEX.getDescription());

		// 6의 경계 5, 7
		assertThatThrownBy(() -> new GoalColorHex("11111"))
				.isInstanceOf(InvalidColorHexException.class)
				.hasMessage(ErrorCode.INVALID_GOAL_COLOR_HEX.getDescription());

		assertThatThrownBy(() -> new GoalColorHex("1111111"))
				.isInstanceOf(InvalidColorHexException.class)
				.hasMessage(ErrorCode.INVALID_GOAL_COLOR_HEX.getDescription());

		// 8의 경계 7(이미 테스트), 9
		assertThatThrownBy(() -> new GoalColorHex("111111111"))
				.isInstanceOf(InvalidColorHexException.class)
				.hasMessage(ErrorCode.INVALID_GOAL_COLOR_HEX.getDescription());
	}

	@Test
	@DisplayName("colorHex 문자열은 [0-9a-fA-F]를 만족해야 한다.")
	void validateInvalidRegexTest() {

		// 3의 경계 2, 4
		assertThatThrownBy(() -> new GoalColorHex("-1"))
				.isInstanceOf(InvalidColorHexException.class)
				.hasMessage(ErrorCode.INVALID_GOAL_COLOR_HEX.getDescription());

		assertThatThrownBy(() -> new GoalColorHex("ggg"))
				.isInstanceOf(InvalidColorHexException.class)
				.hasMessage(ErrorCode.INVALID_GOAL_COLOR_HEX.getDescription());

		assertThatThrownBy(() -> new GoalColorHex("GGG"))
				.isInstanceOf(InvalidColorHexException.class)
				.hasMessage(ErrorCode.INVALID_GOAL_COLOR_HEX.getDescription());
	}
}
