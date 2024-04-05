package com.sillim.recordit.goal.controller.dto.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MonthlyUpdateRequestTest {

	@Test
	@DisplayName("MonthlyAddRequest 엔티티 매핑")
	void toEntityTest() {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest("취뽀하기!", "취업할 때까지 숨 참는다.", 2024, 4, "#83c8ef");
		Member member = new Member();

		MonthlyGoal monthlyGoal = request.toEntity(member);
		assertAll(
				() -> {
					assertThat(monthlyGoal.getTitle()).isEqualTo(request.title());
					assertThat(monthlyGoal.getDescription()).isEqualTo(request.description());
					assertThat(monthlyGoal.getGoalYear()).isEqualTo(request.goalYear());
					assertThat(monthlyGoal.getGoalMonth()).isEqualTo(request.goalMonth());
					assertThat(monthlyGoal.getColorHex()).isEqualTo(request.colorHex());
					assertThat(monthlyGoal.getAchieved()).isEqualTo(false);
					assertThat(monthlyGoal.getDeleted()).isEqualTo(false);
					assertThat(monthlyGoal.getMember()).isEqualTo(member);
				});
	}
}
