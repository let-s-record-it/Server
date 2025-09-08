package com.sillim.recordit.config.security.filter;

import com.sillim.recordit.config.security.handler.AuthenticationExceptionHandler;
import com.sillim.recordit.global.exception.common.ApplicationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuthExceptionTranslationFilter extends OncePerRequestFilter {

	private final AuthenticationExceptionHandler authenticationExceptionHandler;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			doFilter(request, response, filterChain);
		} catch (ApplicationException e) {
			authenticationExceptionHandler.handle(response, e);
		}
	}
}
