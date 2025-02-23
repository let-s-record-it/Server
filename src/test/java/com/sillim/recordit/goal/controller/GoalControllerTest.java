package com.sillim.recordit.goal.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.fixture.WeeklyGoalFixture;
import com.sillim.recordit.goal.service.MonthlyGoalQueryService;
import com.sillim.recordit.goal.service.WeeklyGoalQueryService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(GoalController.class)
public class GoalControllerTest extends RestDocsTest {

	@MockBean MonthlyGoalQueryService monthlyGoalQueryService;
	@MockBean WeeklyGoalQueryService weeklyGoalQueryService;

	private Member member;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
	}

	@Test
	@DisplayName("당월의 목표 목록을 조회한다.")
	void monthlyGoalListTest() throws Exception {
		List<MonthlyGoal> monthlyGoals =
				LongStream.rangeClosed(1, 3)
						.mapToObj(
								(id) -> {
									MonthlyGoal goal =
											spy(MonthlyGoalFixture.DEFAULT.getWithMember(member));
									given(goal.getId()).willReturn(id);
									given(goal.isAchieved()).willReturn(id % 2 == 0);
									return goal;
								})
						.toList();
		given(monthlyGoalQueryService.searchAllByDate(anyInt(), anyInt(), any()))
				.willReturn(monthlyGoals);
		List<WeeklyGoal> weeklyGoals =
				LongStream.rangeClosed(1, 2)
						.mapToObj(
								(id) -> {
									WeeklyGoal goal =
											spy(WeeklyGoalFixture.DEFAULT.getWithMember(member));
									given(goal.getId()).willReturn(id);
									given(goal.isAchieved()).willReturn(id % 2 == 0);
									return goal;
								})
						.toList();
		given(weeklyGoalQueryService.searchAllWeeklyGoalByDate(anyInt(), anyInt(), any()))
				.willReturn(weeklyGoals);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/goals")
								.headers(authorizationHeader())
								.queryParam("year", "2024")
								.queryParam("month", "4"));

		perform.andExpect(status().isOk())
				.andExpect(jsonPath("$.monthlyGoals.size()").value(3))
				.andExpect(jsonPath("$.weeklyGoals.size()").value(2))
				.andExpect(jsonPath("$.monthlyGoals.[0].id").value(1))
				.andExpect(jsonPath("$.monthlyGoals.[0].title").value("취뽀하기!"))
				.andExpect(jsonPath("$.monthlyGoals.[1].id").value(2))
				.andExpect(jsonPath("$.monthlyGoals.[1].title").value("취뽀하기!"))
				.andExpect(jsonPath("$.monthlyGoals.[2].id").value(3))
				.andExpect(jsonPath("$.monthlyGoals.[2].title").value("취뽀하기!"))
				.andExpect(jsonPath("$.weeklyGoals.[0].id").value(1))
				.andExpect(jsonPath("$.weeklyGoals.[0].title").value("데이터베이스 3장까지"))
				.andExpect(jsonPath("$.weeklyGoals.[1].id").value(2))
				.andExpect(jsonPath("$.weeklyGoals.[1].title").value("데이터베이스 3장까지"));

		perform.andDo(print())
				.andDo(
						document(
								"goal-list",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								queryParameters(
										parameterWithName("year").description("조회할 연도"),
										parameterWithName("month").description("조회할 월"))));
	}
}
