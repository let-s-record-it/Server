package com.sillim.recordit.goal.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
public class MonthlyGoalJpaRepositoryTest {

	@Autowired MonthlyGoalJpaRepository monthlyGoalJpaRepository;
	@Autowired TestEntityManager em;

	private Member member;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
		em.persist(member);
	}

	@Test
	@DisplayName("새로운 월 목표 레코드를 저장한다.")
	void saveTest() {
		// given
		final MonthlyGoal expected = MonthlyGoalFixture.DEFAULT.getWithMember(member);
		// when
		MonthlyGoal saved =
				monthlyGoalJpaRepository.save(MonthlyGoalFixture.DEFAULT.getWithMember(member));
		// then
		MonthlyGoal found = em.find(MonthlyGoal.class, saved.getId());

		// 자동 생성 필드가 null이 아닌지 검증
		assertThat(found.getId()).isNotNull();
		assertThat(found.getCreatedAt()).isNotNull();
		assertThat(found.getModifiedAt()).isNotNull();

		assertThat(found)
				.usingRecursiveComparison()
				.ignoringFields("id", "member", "createdAt", "modifiedAt")
				.isEqualTo(expected);
		assertThat(found.getMember()).usingRecursiveComparison().isEqualTo(expected.getMember());
	}

	@Test
	@DisplayName("기존의 월 목표 레코드를 갱신한다.")
	void updateTest() {
		// given
		final MonthlyGoal expected = MonthlyGoalFixture.MODIFIED.getWithMember(member);
		MonthlyGoal saved =
				monthlyGoalJpaRepository.save(MonthlyGoalFixture.DEFAULT.getWithMember(member));

		// when
		saved.modify(
				expected.getTitle(),
				expected.getDescription(),
				expected.getStartDate(),
				expected.getEndDate(),
				expected.getColorHex());

		// then
		MonthlyGoal found = em.find(MonthlyGoal.class, saved.getId());
		assertThat(found)
				.usingRecursiveComparison()
				.ignoringFields("id", "member", "createdAt", "modifiedAt")
				.isEqualTo(expected);
	}

	@Test
	@DisplayName("해당 년, 월에 해당하는 월 목표 목록을 조회한다.")
	void findByGoalYearAndGoalMonthAndMember() {
		// given
		final MonthlyGoal expected =
				MonthlyGoalFixture.DEFAULT.getWithStartDateAndEndDate(
						LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31), member);
		monthlyGoalJpaRepository.saveAll(
				List.of(
						MonthlyGoalFixture.DEFAULT.getWithStartDateAndEndDate(
								LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31), member),
						MonthlyGoalFixture.DEFAULT.getWithStartDateAndEndDate(
								LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31), member),
						MonthlyGoalFixture.DEFAULT.getWithStartDateAndEndDate(
								LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30), member)));
		// when
		List<MonthlyGoal> foundList =
				monthlyGoalJpaRepository.findByPeriod_StartDateAndPeriod_EndDateAndMember(
						expected.getStartDate(), expected.getEndDate(), member);
		// then
		assertThat(foundList).hasSize(2);
		for (MonthlyGoal found : foundList) {
			assertThat(found)
					.usingRecursiveComparison()
					.comparingOnlyFields("goalYear", "goalMonth")
					.isEqualTo(expected);
		}
	}
}
