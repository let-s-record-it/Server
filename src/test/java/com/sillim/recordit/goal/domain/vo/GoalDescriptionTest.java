package com.sillim.recordit.goal.domain.vo;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidDescriptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GoalDescriptionTest {

	@Test
	@DisplayName("GoalDescription을 생성한다.")
	void newGoalTitleTest() {

		assertThatCode(() -> new GoalDescription("취뽀하기!")).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("description은 null이 아니어야 한다.")
	void validateNullTest() {

		assertThatThrownBy(() -> new GoalDescription(null)).isInstanceOf(InvalidDescriptionException.class)
				.hasMessage(ErrorCode.NULL_GOAL_DESCRIPTION.getDescription());
	}

	@Test
	@DisplayName("description은 500자를 넘을 수 없다.")
	void validateInvalidLengthTest() {

		assertThatThrownBy(() -> new GoalDescription("0123456789".repeat(50) + "0"))
				.isInstanceOf(InvalidDescriptionException.class)
				.hasMessage(ErrorCode.INVALID_GOAL_DESCRIPTION_LENGTH.getDescription());
	}
}
