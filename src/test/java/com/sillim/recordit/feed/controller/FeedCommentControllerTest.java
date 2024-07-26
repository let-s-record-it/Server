package com.sillim.recordit.feed.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.feed.dto.request.FeedCommentAddRequest;
import com.sillim.recordit.feed.service.FeedCommentCommandService;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(FeedCommentController.class)
class FeedCommentControllerTest extends RestDocsTest {

    @MockBean
    FeedCommentCommandService feedCommentCommandService;

    @Test
    @DisplayName("피드 댓글을 생성한다.")
    void feedCommentAdd() throws Exception {
        long feedId = 1L;
        long feedCommentId = 1L;
        FeedCommentAddRequest request = new FeedCommentAddRequest("content");
        given(feedCommentCommandService.addFeedComment(eq(request), anyLong(), any()))
                .willReturn(feedCommentId);

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/feeds/{feedId}/comments", feedId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));

        perform.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/feeds/1/comments/1"));

        perform.andDo(print())
                .andDo(document("feed-comment-add", getDocumentRequest(), getDocumentResponse()));
    }
}