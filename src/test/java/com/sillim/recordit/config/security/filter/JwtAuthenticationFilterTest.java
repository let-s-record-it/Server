package com.sillim.recordit.config.security.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.config.security.jwt.JwtValidator;
import com.sillim.recordit.member.domain.AuthorizedUser;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.mapper.AuthorizedUserMapper;
import com.sillim.recordit.member.service.MemberQueryService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@Mock JwtValidator jwtValidator;
	@Mock MemberQueryService memberQueryService;
	@Mock AuthorizedUserMapper authorizedUserMapper;
	@InjectMocks JwtAuthenticationFilter jwtAuthenticationFilter;

	@BeforeEach
	void initSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("헤더에 유효한 Bearer Token이 있으면 인증에 성공한다.")
	void authenticatedIfExistsTokenInHeader() throws ServletException, IOException {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		FilterChain mockFilterChain = mock(FilterChain.class);
		String token = "Bearer token";
		httpServletRequest.addHeader(HttpHeaders.AUTHORIZATION, token);
		long memberId = 1L;
		Member member =
				Member.createNoJobMember(
						"12345", OAuthProvider.KAKAO, "name", "test@mail.com", "https://image.url");
		given(jwtValidator.getMemberIdIfValid(eq("token"))).willReturn(memberId);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(authorizedUserMapper.toAuthorizedUser(member))
				.willReturn(new AuthorizedUser(member, null, null));

		jwtAuthenticationFilter.doFilterInternal(
				httpServletRequest, httpServletResponse, mockFilterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
				.isTrue();
	}

	@Test
	@DisplayName("헤더에 Token이 Bearer Type이 아니면 인증에 실패한다.")
	void notAuthenticatedIfTokenTypeIsNotBearer() throws ServletException, IOException {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		FilterChain mockFilterChain = mock(FilterChain.class);
		String token = "Basic token";
		httpServletRequest.addHeader(HttpHeaders.AUTHORIZATION, token);

		jwtAuthenticationFilter.doFilterInternal(
				httpServletRequest, httpServletResponse, mockFilterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	@DisplayName("헤더에 Token이 없으면 인증에 실패한다.")
	void notAuthenticatedIfNotExistsTokenInHeader() throws ServletException, IOException {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		FilterChain mockFilterChain = mock(FilterChain.class);

		jwtAuthenticationFilter.doFilterInternal(
				httpServletRequest, httpServletResponse, mockFilterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	@DisplayName("헤더에 Token이 유효하지 않으면 인증에 실패한다.")
	void notAuthenticatedIfTokenIsInvalid() throws ServletException, IOException {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		FilterChain mockFilterChain = mock(FilterChain.class);
		String token = "Bearer token";
		httpServletRequest.addHeader(HttpHeaders.AUTHORIZATION, token);
		given(jwtValidator.getMemberIdIfValid(eq("token"))).willThrow(MalformedJwtException.class);

		assertThatThrownBy(
						() ->
								jwtAuthenticationFilter.doFilterInternal(
										httpServletRequest, httpServletResponse, mockFilterChain))
				.isInstanceOf(MalformedJwtException.class);
	}
}
