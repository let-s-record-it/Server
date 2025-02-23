package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.category.service.ScheduleCategoryQueryService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidWeeklyGoalException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.request.WeeklyGoalUpdateRequest;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
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
	@Mock ScheduleCategoryQueryService scheduleCategoryQueryService;
	@InjectMocks WeeklyGoalUpdateService weeklyGoalUpdateService;

	private Member member;
	private ScheduleCategory category;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
		category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member);
	}

	@Test
	@DisplayName("새로운 주 목표를 추가한다. - 연관 월 목표 없음")
	void addWeeklyGoalWithoutRelatedMonthlyGoal() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		Long categoryId = 3L;
		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						categoryId,
						null);
		WeeklyGoal saved = mock(WeeklyGoal.class);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(categoryId)))
				.willReturn(category);
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
		Long categoryId = 4L;
		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						categoryId,
						relatedMonthlyGoalId);
		WeeklyGoal saved = mock(WeeklyGoal.class);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(categoryId)))
				.willReturn(category);
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
		Long categoryId = 3L;
		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						categoryId,
						null);
		WeeklyGoal modified = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(weeklyGoalId), eq(memberId)))
				.willReturn(modified);
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(categoryId)))
				.willReturn(category);

		weeklyGoalUpdateService.modifyWeeklyGoal(request, weeklyGoalId, memberId);

		assertAll(
				() -> {
					assertThat(modified.getTitle()).isEqualTo(request.title());
					assertThat(modified.getDescription()).isEqualTo(request.description());
					assertThat(modified.getWeek()).isEqualTo(request.week());
					assertThat(modified.getStartDate()).isEqualTo(request.startDate());
					assertThat(modified.getEndDate()).isEqualTo(request.endDate());
					assertThat(modified.getRelatedMonthlyGoal()).isEmpty();
				});
	}

	@Test
	@DisplayName("id에 해당하는 주 목표를 수정한다. - 연관 월 목표 있음")
	void modifyWeeklyGoalWithRelatedMonthlyGoal() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		Long relatedMonthlyGoalId = 3L;
		Long categoryId = 4L;
		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						categoryId,
						relatedMonthlyGoalId);
		WeeklyGoal modified = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(weeklyGoalId), eq(memberId)))
				.willReturn(modified);
		MonthlyGoal relatedMonthlyGoal = mock(MonthlyGoal.class);
		given(
						monthlyGoalQueryService.searchByIdAndCheckAuthority(
								eq(relatedMonthlyGoalId), eq(memberId)))
				.willReturn(relatedMonthlyGoal);
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(categoryId)))
				.willReturn(category);

		weeklyGoalUpdateService.modifyWeeklyGoal(request, weeklyGoalId, memberId);

		assertAll(
				() -> {
					assertThat(modified.getTitle()).isEqualTo(request.title());
					assertThat(modified.getDescription()).isEqualTo(request.description());
					assertThat(modified.getWeek()).isEqualTo(request.week());
					assertThat(modified.getStartDate()).isEqualTo(request.startDate());
					assertThat(modified.getEndDate()).isEqualTo(request.endDate());
					assertThat(modified.getRelatedMonthlyGoal()).isNotEmpty();
				});
	}

	@Test
	@DisplayName("id에 해당하는 주 목표의 달성 상태를 변경한다.")
	void changeAchieveStatus() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		Boolean status = true;
		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(weeklyGoalId), eq(memberId)))
				.willReturn(weeklyGoal);

		weeklyGoalUpdateService.changeAchieveStatus(weeklyGoalId, status, memberId);

		assertThat(weeklyGoal.isAchieved()).isTrue();
	}

	@Test
	@DisplayName("id에 해당하는 주 목표를 삭제한다.")
	void removeTest() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(weeklyGoalId), eq(memberId)))
				.willReturn(weeklyGoal);

		weeklyGoalUpdateService.remove(weeklyGoalId, memberId);

		assertThat(weeklyGoal.isDeleted()).isTrue();
	}

	@Test
	@DisplayName("id에 해당하는 주 목표를 월 목표와 연결한다.")
	void linkRelatedMonthlyGoal() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		Long relatedGoalId = 3L;
		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(weeklyGoalId), eq(memberId)))
				.willReturn(weeklyGoal);
		MonthlyGoal relatedMonthlyGoal = MonthlyGoalFixture.DEFAULT.getWithMember(category, member);
		given(monthlyGoalQueryService.searchByIdAndCheckAuthority(eq(relatedGoalId), eq(memberId)))
				.willReturn(relatedMonthlyGoal);

		weeklyGoalUpdateService.linkRelatedMonthlyGoal(weeklyGoalId, relatedGoalId, memberId);

		assertThat(weeklyGoal.getRelatedMonthlyGoal()).isNotEmpty();
		assertThat(weeklyGoal.getRelatedMonthlyGoal().get())
				.usingRecursiveComparison()
				.isEqualTo(relatedMonthlyGoal);
	}

	@Test
	@DisplayName("id에 해당하는 주 목표를 월 목표와 연결한다.")
	void unlinkRelatedMonthlyGoal() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		weeklyGoal.linkRelatedMonthlyGoal(
				MonthlyGoalFixture.DEFAULT.getWithMember(category, member));
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(weeklyGoalId), eq(memberId)))
				.willReturn(weeklyGoal);

		weeklyGoalUpdateService.unlinkRelatedMonthlyGoal(weeklyGoalId, memberId);

		assertThat(weeklyGoal.getRelatedMonthlyGoal()).isEmpty();
	}

	@Test
	@DisplayName("주 목표의 연관 목표가 없는 상태에서 연결 해제를 시도할 경우, InvalidWeeklyGoalException이 발생한다.")
	void unlinkRelatedMonthlyGoalThrowsInvalidWeeklyGoalExceptionIfRelatedGoalIsNotExists() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(eq(weeklyGoalId), eq(memberId)))
				.willReturn(weeklyGoal);

		assertThatCode(
						() -> {
							weeklyGoalUpdateService.unlinkRelatedMonthlyGoal(
									weeklyGoalId, memberId);
						})
				.isInstanceOf(InvalidWeeklyGoalException.class)
				.hasMessage(ErrorCode.RELATED_GOAL_NOT_FOUND.getDescription());
	}
}
