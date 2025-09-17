package com.sillim.recordit.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.global.dto.response.ErrorResponse;
import com.sillim.recordit.global.exception.ErrorCode;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiThrottlingFilter implements Filter {

	private static final List<LimitApi> LIMIT_APIS =
			List.of(
					LimitApi.pattern("/api/v1/invite/members/*"),
					LimitApi.pattern("POST", "/api/v1/members/*/follow"),
					LimitApi.pattern("POST", "/api/v1/feeds/**"));
	public static final int TOO_MANY_REQUEST = 429;

	private final ProxyManager<String> proxyManager;
	private final BucketConfiguration bucketConfiguration;
	private final AntPathMatcher antPathMatcher = new AntPathMatcher();
	private final ObjectMapper objectMapper;

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

		Bucket bucket = proxyManager.getProxy(auth, () -> bucketConfiguration);
		for (LimitApi limitApi : LIMIT_APIS) {
			if (antPathMatcher.match(limitApi.getUrl(), request.getRequestURI())
					&& (limitApi.noMethod() || limitApi.getMethod().equals(request.getMethod()))) {
				checkApiToken(bucket, filterChain, servletRequest, servletResponse);
				return;
			}
		}

		filterChain.doFilter(servletRequest, servletResponse);
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
			return;
		}

		long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setContentType("text/plain; charset=UTF-8");
		httpResponse.setStatus(TOO_MANY_REQUEST);
		httpResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

		response.getWriter()
				.write(
						objectMapper.writeValueAsString(
								ResponseEntity.status(TOO_MANY_REQUEST)
										.body(
												ErrorResponse.from(
														ErrorCode.TOO_MANY_REQUEST,
														waitForRefill + "초 뒤에 다시 시도해주세요"))));
	}
}
