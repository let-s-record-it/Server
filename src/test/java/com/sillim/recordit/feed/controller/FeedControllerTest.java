package com.sillim.recordit.feed.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.dto.response.FeedDetailsResponse;
import com.sillim.recordit.feed.dto.response.FeedInListResponse;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.service.*;
import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(FeedController.class)
class FeedControllerTest extends RestDocsTest {

	@MockBean FeedCommandService feedCommandService;
	@MockBean FeedQueryService feedQueryService;
	@MockBean FeedLikeService feedLikeService;
	@MockBean FeedScrapService feedScrapService;

	@Test
	@DisplayName("피드를 생성한다.")
	void feedAdd() throws Exception {
		FeedAddRequest request = new FeedAddRequest("title", "content");
		long feedId = 1L;
		MockMultipartFile multipartFile1 =
				new MockMultipartFile(
						"images",
						"image1.jpg",
						"text/plain",
						"test1".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile multipartFile2 =
				new MockMultipartFile(
						"images",
						"image2.jpg",
						"text/plain",
						"test2".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile feedAddRequest =
				new MockMultipartFile(
						"feedAddRequest", "", "application/json", toJson(request).getBytes());
		given(feedCommandService.addFeed(eq(request), anyList(), any())).willReturn(feedId);

		ResultActions perform =
				mockMvc.perform(
						multipart("/api/v1/feeds", feedId)
								.file(multipartFile1)
								.file(multipartFile2)
								.file(feedAddRequest));

		perform.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/api/v1/feeds/1"));

		perform.andDo(print())
				.andDo(document("feed-add", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("상세 피드를 조회한다.")
	void feedDetails() throws Exception {
		long feedId = 1L;
		Member member = mock(Member.class);
		Feed feed = FeedFixture.DEFAULT.getFeed(member);
		FeedDetailsResponse feedResponse = FeedDetailsResponse.of(feed, 1L, false, false);
		given(member.equalsId(any())).willReturn(true);
		given(feedQueryService.searchById(any(), any())).willReturn(feedResponse);

		ResultActions perform = mockMvc.perform(get("/api/v1/feeds/{feedId}", feedId));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(document("feed-details", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("피드 목록을 조회한다.")
	void feedList() throws Exception {
		Member member = mock(Member.class);
		Feed feed = spy(FeedFixture.DEFAULT.getFeed(member));
		given(feed.getId()).willReturn(1L);
		SliceResponse<FeedInListResponse> response =
				SliceResponse.of(
						new SliceImpl<>(
								List.of(FeedInListResponse.from(feed, false, false)),
								PageRequest.of(0, 10),
								false));
		given(member.equalsId(any())).willReturn(true);
		given(feedQueryService.searchRecentCreated(any(), any())).willReturn(response);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/feeds").queryParam("page", "0").queryParam("size", "10"));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(document("feed-list", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("자신의 피드 목록을 조회한다.")
	void myFeedList() throws Exception {
		Member member = mock(Member.class);
		Feed feed = spy(FeedFixture.DEFAULT.getFeed(member));
		given(feed.getId()).willReturn(1L);
		SliceResponse<FeedInListResponse> response =
				SliceResponse.of(
						new SliceImpl<>(
								List.of(FeedInListResponse.from(feed, false, false)),
								PageRequest.of(0, 10),
								false));
		given(member.equalsId(any())).willReturn(true);
		given(feedQueryService.searchRecentCreatedByMemberId(any(), any())).willReturn(response);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/feeds/my-feed")
								.queryParam("page", "0")
								.queryParam("size", "10"));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(document("my-feed-list", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("피드를 삭제한다.")
	void feedRemove() throws Exception {
		long feedId = 1L;
		ResultActions perform = mockMvc.perform(delete("/api/v1/feeds/{feedId}", feedId));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(document("feed-remove", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("피드 좋아요를 한다.")
	void feedLike() throws Exception {
		long feedId = 1L;
		ResultActions perform = mockMvc.perform(post("/api/v1/feeds/{feedId}/like", feedId));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(document("feed-like", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("피드 좋아요를 취소한다.")
	void feedUnlike() throws Exception {
		long feedId = 1L;
		ResultActions perform = mockMvc.perform(delete("/api/v1/feeds/{feedId}/unlike", feedId));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(document("feed-unlike", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("피드 스크랩를 한다.")
	void feedScrap() throws Exception {
		long feedId = 1L;
		ResultActions perform = mockMvc.perform(post("/api/v1/feeds/{feedId}/scrap", feedId));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(document("feed-scrap", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("피드 스크랩를 취소한다.")
	void feedUnScrap() throws Exception {
		long feedId = 1L;
		ResultActions perform = mockMvc.perform(delete("/api/v1/feeds/{feedId}/unscrap", feedId));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(document("feed-unscrap", getDocumentRequest(), getDocumentResponse()));
	}
}
