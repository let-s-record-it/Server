package com.sillim.recordit.config.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Slf4j
@Component
public class ApiThrottlingFilter implements Filter {

	private static final List<LimitApi> LIMIT_APIS =
			List.of(
					LimitApi.pattern("/api/v1/invite/members/*"),
					LimitApi.pattern("POST", "/api/v1/members/*/follow"),
					LimitApi.pattern("POST", "/api/v1/feeds/**"));

	private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
	private final AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;

		String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (auth == null) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		Bucket bucket =
				buckets.computeIfAbsent(
						auth, id -> createIntervalBucket(5, 5, Duration.ofSeconds(5)));
		for (LimitApi limitApi : LIMIT_APIS) {
			if (antPathMatcher.match(limitApi.getUrl(), request.getRequestURI())
					&& (limitApi.noMethod() || limitApi.getMethod().equals(request.getMethod()))) {
				checkApiToken(bucket, filterChain, servletRequest, servletResponse);
				return;
			}
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	private Bucket createIntervalBucket(int capacity, int refill, Duration duration) {
		return Bucket.builder()
				.addLimit(limit -> limit.capacity(capacity).refillIntervally(refill, duration))
				.build();
	}

	private void checkApiToken(
			Bucket bucket,
			FilterChain filterChain,
			ServletRequest request,
			ServletResponse response)
			throws IOException, ServletException {
		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

		if (probe.isConsumed()) {
			filterChain.doFilter(request, response);
		} else {
			long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.setContentType("text/plain; charset=UTF-8");
			httpResponse.setStatus(429);
			response.getWriter().write(waitForRefill + "초 뒤에 다시 시도해주세요");
		}
	}
}
