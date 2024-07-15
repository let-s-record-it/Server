package com.sillim.recordit.feed.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.service.FeedCommandService;
import com.sillim.recordit.feed.service.FeedImageUploadService;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(FeedController.class)
class FeedControllerTest extends RestDocsTest {

	@MockBean FeedImageUploadService feedImageUploadService;
	@MockBean FeedCommandService feedCommandService;

	@Test
	@DisplayName("피드를 생성한다.")
	void feedAdd() throws Exception {
		FeedAddRequest request = new FeedAddRequest("title", "content");
		long feedId = 1L;
		given(feedCommandService.addFeed(eq(request), any())).willReturn(feedId);

		ResultActions perform =
				mockMvc.perform(
						post("/api/v1/feeds")
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/api/v1/feeds/1"));

		perform.andDo(print())
				.andDo(document("feed-add", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("피드 이미지를 등록한다.")
	void feedImagesAdd() throws Exception {
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
		long feedId = 1L;

		ResultActions perform =
				mockMvc.perform(
						multipart("/api/v1/feeds/{feedId}/images", feedId)
								.file(multipartFile1)
								.file(multipartFile2));

		perform.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/api/v1/feeds/1/images"));

		perform.andDo(print())
				.andDo(document("feed-images-add", getDocumentRequest(), getDocumentResponse()));
	}
}
