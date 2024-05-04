package com.sillim.recordit.goal.controller.dto.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MonthlyUpdateRequestTest {

	@Test
	@DisplayName("MonthlyAddRequest 엔티티 매핑")
	void toEntityTest() {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						LocalDate.of(2024, 4, 1),
						LocalDate.of(2024, 4, 30),
						"ff83c8ef");
		Member member = MemberFixture.DEFAULT.getMember();

		MonthlyGoal monthlyGoal = request.toEntity(member);
		assertAll(
				() -> {
					assertThat(monthlyGoal.getTitle()).isEqualTo(request.title());
					assertThat(monthlyGoal.getDescription()).isEqualTo(request.description());
					assertThat(monthlyGoal.getStartDate()).isEqualTo(request.startDate());
					assertThat(monthlyGoal.getEndDate()).isEqualTo(request.endDate());
					assertThat(monthlyGoal.getColorHex()).isEqualTo(request.colorHex());
					assertThat(monthlyGoal.isAchieved()).isEqualTo(false);
					assertThat(monthlyGoal.getMember()).isEqualTo(member);
				});
	}
}
