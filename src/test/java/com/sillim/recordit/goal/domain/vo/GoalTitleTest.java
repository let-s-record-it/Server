package com.sillim.recordit.goal.domain.vo;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidTitleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GoalTitleTest {

	@Test
	@DisplayName("GoalTitle을 생성한다.")
	void newGoalTitleTest() {

		assertThatCode(() -> new GoalTitle("취뽀하기!")).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("title은 null이 아니어야 한다.")
	void validateNullTest() {

		assertThatThrownBy(() -> new GoalTitle(null))
				.isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.NULL_GOAL_TITLE.getDescription());
	}

	@Test
	@DisplayName("title은 비어있을 수 없다.")
	void validateBlankTest() {

		assertThatThrownBy(() -> new GoalTitle(""))
				.isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.BLANK_GOAL_TITLE.getDescription());
		assertThatThrownBy(() -> new GoalTitle(" "))
				.isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.BLANK_GOAL_TITLE.getDescription());
	}

	@Test
	@DisplayName("title은 30자를 넘을 수 없다.")
	void validateInvalidLengthTest() {

		assertThatThrownBy(() -> new GoalTitle("0123456789".repeat(3) + "0"))
				.isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.INVALID_GOAL_TITLE_LENGTH.getDescription());
	}
}
