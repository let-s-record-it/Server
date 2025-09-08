package com.sillim.recordit.config.security.oauth2;

import com.sillim.recordit.config.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtProvider jwtProvider;

	@Value("${client.url}")
	private String clientUrl;

	@Value("${client.endpoint}")
	private String redirectEndpoint;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		LoginMember loginMember = (LoginMember) authentication.getPrincipal();
		getRedirectStrategy().sendRedirect(request, response,
				clientUrl + redirectEndpoint + "?token=" + jwtProvider.generateExchangeToken(loginMember.getId()));
	}
}
