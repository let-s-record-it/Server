package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.fixture.WeeklyGoalFixture;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WeeklyGoalQueryServiceTest {

	@Mock WeeklyGoalRepository weeklyGoalRepository;
	@InjectMocks WeeklyGoalQueryService weeklyGoalQueryService;

	private Member member;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
	}

	@Test
	@DisplayName("startDate와 endDate를 기반으로 해당 주 목표들을 조회한다.")
	void searchAllWeeklyGoalByDate() {
		Long memberId = 1L;
		Integer year = 2024;
		Integer month = 8;
		List<WeeklyGoal> weeklyGoals =
				LongStream.rangeClosed(1, 3)
						.mapToObj(
								(id) ->
										WeeklyGoalFixture.DEFAULT.getWithWeekAndStartDateAndEndDate(
												3,
												LocalDate.of(2024, 8, 11),
												LocalDate.of(2024, 8, 17),
												member))
						.toList();
		given(weeklyGoalRepository.findWeeklyGoalInMonth(eq(year), eq(month), eq(memberId)))
				.willReturn(weeklyGoals);

		List<WeeklyGoal> found =
				weeklyGoalQueryService.searchAllWeeklyGoalByDate(year, month, memberId);

		assertAll(
				() -> {
					assertThat(found).hasSize(weeklyGoals.size());
					found.forEach(wg -> assertThat(wg.getStartDate()).hasYear(year));
					found.forEach(wg -> assertThat(wg.getStartDate()).hasMonth(Month.of(month)));
				});
	}
}
