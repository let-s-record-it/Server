package com.sillim.recordit.member.controller;

import com.sillim.recordit.support.restdocs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.ResultActions;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends RestDocsTest {

    @Test
    @DisplayName("자신의 정보를 조회한다.")
    void feedDetails() throws Exception {
        ResultActions perform = mockMvc.perform(get("/api/v1/members/me"));

        perform.andExpect(status().isOk());

        perform.andDo(print())
                .andDo(document("my-profile", getDocumentRequest(), getDocumentResponse()));
    }
}