package com.sillim.recordit.member.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.feed.service.FeedCommentQueryService;
import com.sillim.recordit.member.dto.request.ProfileModifyRequest;
import com.sillim.recordit.member.service.*;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends RestDocsTest {

	@MockBean
	MemberCommandService memberCommandService;
	@MockBean
	FeedCommentQueryService feedCommentQueryService;
	@MockBean
	MemberDeviceService memberDeviceService;
	@MockBean
	MemberFollowService memberFollowService;
	@MockBean
	MemberQueryService memberQueryService;
	@MockBean
	MemberRecommender memberRecommender;

	@Test
	@DisplayName("자신의 정보를 조회한다.")
	void myDetails() throws Exception {
		ResultActions perform = mockMvc.perform(get("/api/v1/members/me"));

		perform.andExpect(status().isOk());

		perform.andDo(print()).andDo(document("my-profile", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("프로필 정보를 수정한다.")
	void profileModify() throws Exception {
		ProfileModifyRequest request = new ProfileModifyRequest("name", "job");
		ResultActions perform = mockMvc
				.perform(put("/api/v1/members/me").content(toJson(request)).contentType(MediaType.APPLICATION_JSON));

		perform.andExpect(status().isNoContent());

		perform.andDo(print()).andDo(document("profile-modify", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("자신의 피드 댓글들을 조회한다.")
	void myFeedComments() throws Exception {
		ResultActions perform = mockMvc.perform(get("/api/v1/members/me/comments"));

		perform.andExpect(status().isOk());

		perform.andDo(print()).andDo(document("my-feed-comments", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("회원 탈퇴한다.")
	void withdraw() throws Exception {
		ResultActions perform = mockMvc.perform(delete("/api/v1/members/me/withdraw"));

		perform.andExpect(status().isNoContent());

		perform.andDo(print()).andDo(document("member-withdraw", getDocumentRequest(), getDocumentResponse()));
	}
}
