package com.sillim.recordit.config.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class ApiThrottlingFilterTest {

	@Mock ProxyManager<String> proxyManager;
	@Mock BucketConfiguration bucketConfiguration;
	@InjectMocks ApiThrottlingFilter apiThrottlingFilter;

	@Test
	@DisplayName("URL이 제한 API 리스트에 매칭되지 않으면 필터 체인이 동작한다.")
	void calledFilterChainIfURLNotInAPILimitList() throws ServletException, IOException {
		MockHttpServletRequest httpServletRequest =
				new MockHttpServletRequest("POST", "/api/v1/invite");
		httpServletRequest.addHeader("Authorization", "Bearer test1");
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);
		BucketProxy bucketProxy = mock(BucketProxy.class);
		given(proxyManager.getProxy(anyString(), any())).willReturn(bucketProxy);

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
		BucketProxy bucketProxy = mock(BucketProxy.class);
		AtomicInteger counter = new AtomicInteger();
		given(proxyManager.getProxy(anyString(), any())).willReturn(bucketProxy);
		given(bucketProxy.tryConsumeAndReturnRemaining(eq(1L)))
				.willAnswer(
						invo -> {
							int call = counter.incrementAndGet();
							if (call >= 6) {
								return ConsumptionProbe.rejected(0, 5_000_000_000L, 5_000_000_000L);
							}
							return ConsumptionProbe.consumed(1, 5_000_000_000L);
						});

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
		BucketProxy bucketProxy = mock(BucketProxy.class);
		AtomicInteger counter = new AtomicInteger();
		given(proxyManager.getProxy(anyString(), any())).willReturn(bucketProxy);
		given(bucketProxy.tryConsumeAndReturnRemaining(eq(1L)))
				.willAnswer(
						invo -> {
							int call = counter.incrementAndGet();
							if (call >= 6) {
								return ConsumptionProbe.rejected(0, 5_000_000_000L, 5_000_000_000L);
							}
							return ConsumptionProbe.consumed(1, 5_000_000_000L);
						});

		for (int i = 0; i < 6; i++) {
			apiThrottlingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		}

		verify(filterChain, times(5)).doFilter(httpServletRequest, httpServletResponse);
		assertThat(httpServletResponse.getStatus()).isEqualTo(429);
	}
}
