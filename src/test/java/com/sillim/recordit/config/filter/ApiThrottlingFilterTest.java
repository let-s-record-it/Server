package com.sillim.recordit.config.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class ApiThrottlingFilterTest {

	ApiThrottlingFilter apiThrottlingFilter = new ApiThrottlingFilter();

	@Test
	@DisplayName("URL이 제한 API 리스트에 매칭되지 않으면 필터 체인이 동작한다.")
	void calledFilterChainIfURLNotInAPILimitList() throws ServletException, IOException {
		MockHttpServletRequest httpServletRequest =
				new MockHttpServletRequest("POST", "/api/v1/invite");
		httpServletRequest.addHeader("Authorization", "Bearer test1");
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		for (int i = 0; i < 6; i++) {
			apiThrottlingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		}

		verify(filterChain, times(6)).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	@DisplayName("버킷 제한보다 적은 요청을 보내면 필터 체인이 동작한다.")
	void calledFilterChainIfSendRequestLessThanBucketLimit() throws ServletException, IOException {
		MockHttpServletRequest httpServletRequest =
				new MockHttpServletRequest("POST", "/api/v1/invite/members/1");
		httpServletRequest.addHeader("Authorization", "Bearer test2");
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		for (int i = 0; i < 4; i++) {
			apiThrottlingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		}

		verify(filterChain, times(4)).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	@DisplayName("버킷 제한보다 많은 요청을 보내면 429 statusCode가 설정된다.")
	void statusCodeSet429IfSendRequestMoreThanBucketLimit() throws ServletException, IOException {
		MockHttpServletRequest httpServletRequest =
				new MockHttpServletRequest("POST", "/api/v1/feeds/1");
		httpServletRequest.addHeader("Authorization", "Bearer test3");
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		for (int i = 0; i < 6; i++) {
			apiThrottlingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		}

		verify(filterChain, times(5)).doFilter(httpServletRequest, httpServletResponse);
		assertThat(httpServletResponse.getStatus()).isEqualTo(429);
	}
}
