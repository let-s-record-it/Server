package com.sillim.recordit.goal.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class MonthlyGoalJpaRepositoryTest {

	@Autowired MonthlyGoalJpaRepository monthlyGoalJpaRepository;
	@Autowired TestEntityManager em;

	// TODO: Member 엔티티 구현 완료 후 변경
	Member member = new Member();

	@BeforeEach
	void beforeEach() {
		em.persist(member);
	}

	@Test
	@DisplayName("월 목표 저장 repository 테스트")
	void saveTest() {
		// given
		final MonthlyGoal expected = MonthlyGoalFixture.DEFAULT.getWithMember(member);
		// when
		MonthlyGoal saved =
				monthlyGoalJpaRepository.save(MonthlyGoalFixture.DEFAULT.getWithMember(member));
		// then
		MonthlyGoal found = em.find(MonthlyGoal.class, saved.getId());
		assertThat(found.getId()).isNotNull();
		assertThat(found)
				.usingRecursiveComparison()
				.ignoringFields("id", "member")
				.isEqualTo(expected);
		assertThat(found.getMember()).usingRecursiveComparison().isEqualTo(expected.getMember());
	}

	@Test
	@DisplayName("월 목표 수정 repository 테스트")
	void updateTest() {
		// given
		final MonthlyGoal expected = MonthlyGoalFixture.MODIFIED.getWithMember(member);
		MonthlyGoal saved =
				monthlyGoalJpaRepository.save(MonthlyGoalFixture.DEFAULT.getWithMember(member));

		// when
		saved.modify(
				expected.getTitle(),
				expected.getDescription(),
				expected.getGoalYear(),
				expected.getGoalMonth(),
				expected.getColorHex());

		// then
		MonthlyGoal found = em.find(MonthlyGoal.class, saved.getId());
		assertThat(found)
				.usingRecursiveComparison()
				.ignoringFields("id", "member")
				.isEqualTo(expected);
	}
}
