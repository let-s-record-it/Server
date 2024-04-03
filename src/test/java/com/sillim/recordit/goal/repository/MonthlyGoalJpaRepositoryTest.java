package com.sillim.recordit.goal.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MonthlyGoalJpaRepositoryTest {

	@Autowired MonthlyGoalJpaRepository monthlyGoalJpaRepository;

	// TODO: Member 엔티티 구현 완료 후 변경
	Member member = new Member();

	@Test
	@DisplayName("월 목표 저장 테스트")
	void saveTest() {

		MonthlyGoal monthlyGoal = MonthlyGoalFixture.MONTHLY_GOAL.getWithMember(member);

		MonthlyGoal saved = monthlyGoalJpaRepository.save(monthlyGoal);

		assertAll(
				() -> {
					assertThat(saved.getTitle()).isEqualTo("취뽀하기!");
					assertThat(saved.getDescription()).isEqualTo("취업할 때까지 숨 참는다!");
					assertThat(saved.getGoalYear()).isEqualTo(2024);
					assertThat(saved.getGoalMonth()).isEqualTo(5);
					assertThat(saved.getColorHex()).isEqualTo("#83c8ef");
					assertThat(saved.getAchieved()).isEqualTo(false);
					assertThat(saved.getDeleted()).isEqualTo(false);
					assertThat(saved.getMember()).isEqualTo(member);
				});
	}
}
