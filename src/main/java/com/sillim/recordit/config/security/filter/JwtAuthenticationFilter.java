package com.sillim.recordit.config.security.filter;

import com.sillim.recordit.config.security.jwt.JwtValidator;
import com.sillim.recordit.member.domain.AuthorizedUser;
import com.sillim.recordit.member.mapper.AuthorizedUserMapper;
import com.sillim.recordit.member.service.MemberQueryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	public static final String BEARER = "Bearer ";

	private final JwtValidator jwtValidator;
	private final AuthorizedUserMapper authorizedUserMapper;
	private final MemberQueryService memberQueryService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		getTokenFromHeader(request).ifPresent(this::authenticate);

		doFilter(request, response, filterChain);
	}

	private void authenticate(String token) {
		if (isBearerType(token)) {
			AuthorizedUser authorizedUser = getAuthorizedUser(token);
			SecurityContextHolder.getContext().setAuthentication(
					new UsernamePasswordAuthenticationToken(authorizedUser, "", authorizedUser.getAuthorities()));
		}
	}

	private AuthorizedUser getAuthorizedUser(String token) {
		return authorizedUserMapper.toAuthorizedUser(
				memberQueryService.findByMemberId(jwtValidator.getMemberIdIfValid(token.substring(BEARER.length()))));
	}

	private Optional<String> getTokenFromHeader(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
	}

	private boolean isBearerType(String token) {
		return token.startsWith(BEARER);
	}
}
