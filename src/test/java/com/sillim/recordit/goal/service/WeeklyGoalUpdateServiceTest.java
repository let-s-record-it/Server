package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.request.WeeklyGoalUpdateRequest;
import com.sillim.recordit.goal.fixture.WeeklyGoalFixture;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WeeklyGoalUpdateServiceTest {

	@Mock WeeklyGoalQueryService weeklyGoalQueryService;
	@Mock MonthlyGoalQueryService monthlyGoalQueryService;
	@Mock MemberQueryService memberQueryService;
	@Mock WeeklyGoalRepository weeklyGoalRepository;
	@InjectMocks WeeklyGoalUpdateService weeklyGoalUpdateService;
	private Member member;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
	}

	@Test
	@DisplayName("새로운 주 목표를 추가한다. - 연관 월 목표 없음")
	void addWeeklyGoalWithoutRelatedMonthlyGoal() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						"ff83c8ef",
						null);
		WeeklyGoal saved = mock(WeeklyGoal.class);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(weeklyGoalRepository.save(any(WeeklyGoal.class))).willReturn(saved);
		given(saved.getId()).willReturn(weeklyGoalId);

		Long savedId = weeklyGoalUpdateService.addWeeklyGoal(request, memberId);

		assertThat(savedId).isEqualTo(weeklyGoalId);
		then(weeklyGoalRepository).should(times(1)).save(any(WeeklyGoal.class));
	}

	@Test
	@DisplayName("새로운 주 목표를 추가한다. - 연관 월 목표 있음")
	void addWeeklyGoalWithRelatedMonthlyGoal() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		Long relatedMonthlyGoalId = 3L;
		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						"ff83c8ef",
						relatedMonthlyGoalId);
		WeeklyGoal saved = mock(WeeklyGoal.class);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(monthlyGoalQueryService.searchByIdAndCheckAuthority(relatedMonthlyGoalId, memberId))
				.willReturn(mock(MonthlyGoal.class));
		given(weeklyGoalRepository.save(any(WeeklyGoal.class))).willReturn(saved);
		given(saved.getId()).willReturn(weeklyGoalId);

		Long savedId = weeklyGoalUpdateService.addWeeklyGoal(request, memberId);

		assertThat(savedId).isEqualTo(weeklyGoalId);
		then(weeklyGoalRepository).should(times(1)).save(any(WeeklyGoal.class));
	}

	@Test
	@DisplayName("id에 해당하는 주 목표를 수정한다. - 연관 월 목표 없음")
	void modifyWeeklyGoalWithoutRelatedMonthlyGoal() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						"ff83c8ef",
						null);
		WeeklyGoal modified = WeeklyGoalFixture.DEFAULT.getWithMember(member);
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(weeklyGoalId), eq(memberId)))
				.willReturn(modified);

		weeklyGoalUpdateService.modifyWeeklyGoal(request, weeklyGoalId, memberId);

		assertAll(
				() -> {
					assertThat(modified.getTitle()).isEqualTo(request.title());
					assertThat(modified.getDescription()).isEqualTo(request.description());
					assertThat(modified.getWeek()).isEqualTo(request.week());
					assertThat(modified.getStartDate()).isEqualTo(request.startDate());
					assertThat(modified.getEndDate()).isEqualTo(request.endDate());
					assertThat(modified.getColorHex()).isEqualTo(request.colorHex());
					assertThat(modified.getRelatedMonthlyGoal()).isEmpty();
				});
	}

	@Test
	@DisplayName("id에 해당하는 주 목표를 수정한다. - 연관 월 목표 있음")
	void modifyWeeklyGoalWithRelatedMonthlyGoal() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		Long relatedMonthlyGoalId = 3L;
		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						"ff83c8ef",
						relatedMonthlyGoalId);
		WeeklyGoal modified = WeeklyGoalFixture.DEFAULT.getWithMember(member);
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(weeklyGoalId), eq(memberId)))
				.willReturn(modified);
		MonthlyGoal relatedMonthlyGoal = mock(MonthlyGoal.class);
		given(
						monthlyGoalQueryService.searchByIdAndCheckAuthority(
								eq(relatedMonthlyGoalId), eq(memberId)))
				.willReturn(relatedMonthlyGoal);

		weeklyGoalUpdateService.modifyWeeklyGoal(request, weeklyGoalId, memberId);

		assertAll(
				() -> {
					assertThat(modified.getTitle()).isEqualTo(request.title());
					assertThat(modified.getDescription()).isEqualTo(request.description());
					assertThat(modified.getWeek()).isEqualTo(request.week());
					assertThat(modified.getStartDate()).isEqualTo(request.startDate());
					assertThat(modified.getEndDate()).isEqualTo(request.endDate());
					assertThat(modified.getColorHex()).isEqualTo(request.colorHex());
					assertThat(modified.getRelatedMonthlyGoal()).isNotEmpty();
				});
	}

	@Test
	@DisplayName("id에 해당하는 주 목표의 달성 상태를 변경한다.")
	void changeAchieveStatus() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		Boolean status = true;
		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(member);
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(monthlyGoalId), eq(memberId)))
				.willReturn(weeklyGoal);

		weeklyGoalUpdateService.changeAchieveStatus(monthlyGoalId, status, memberId);

		assertThat(weeklyGoal.isAchieved()).isTrue();
	}

	@Test
	@DisplayName("id에 해당하는 주 목표를 삭제한다.")
	void removeTest() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(member);
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(monthlyGoalId), eq(memberId)))
				.willReturn(weeklyGoal);

		weeklyGoalUpdateService.remove(monthlyGoalId, memberId);

		assertThat(weeklyGoal.isDeleted()).isTrue();
	}
}
