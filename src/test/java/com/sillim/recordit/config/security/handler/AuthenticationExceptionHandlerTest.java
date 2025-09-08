package com.sillim.recordit.config.security.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;

class AuthenticationExceptionHandlerTest {

	AuthenticationExceptionHandler authenticationExceptionHandler = new AuthenticationExceptionHandler(
			new ObjectMapper());

	@Test
	@DisplayName("ApplicationException을 response를 통해 출력한다.")
	void responseApplicationException() throws IOException {
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		ApplicationException exception = new ApplicationException(ErrorCode.UNHANDLED_EXCEPTION);

		authenticationExceptionHandler.handle(httpServletResponse, exception);

		assertThat(httpServletResponse.getStatus()).isEqualTo(401);
		assertThat(httpServletResponse.getContentType()).isEqualTo("application/json;charset=UTF-8");
		assertThat(httpServletResponse.getCharacterEncoding()).isEqualTo("UTF-8");
	}

	@Test
	@DisplayName("AuthenticationException을 response를 통해 출력한다.")
	void responseAuthenticationException() throws IOException {
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		AuthenticationException exception = new InsufficientAuthenticationException("인증이 필요한 URI입니다.");

		authenticationExceptionHandler.handle(httpServletResponse, exception);

		assertThat(httpServletResponse.getStatus()).isEqualTo(401);
		assertThat(httpServletResponse.getContentType()).isEqualTo("application/json;charset=UTF-8");
		assertThat(httpServletResponse.getCharacterEncoding()).isEqualTo("UTF-8");
	}

	@Test
	@DisplayName("ApplicationException 처리 시 response가 commit되어 있다면 출력되지 않는다.")
	void notWriteIfApplicationExceptionResponseCommitted() throws IOException {
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		AuthenticationException exception = new InsufficientAuthenticationException("인증이 필요한 URI입니다.");
		httpServletResponse.setCommitted(true);

		authenticationExceptionHandler.handle(httpServletResponse, exception);

		assertThat(httpServletResponse.getStatus()).isEqualTo(200);
	}

	@Test
	@DisplayName("AuthenticationException 처리 시 response가 commit되어 있다면 출력되지 않는다.")
	void notWriteIfAuthenticationExceptionResponseCommitted() throws IOException {
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		ApplicationException exception = new ApplicationException(ErrorCode.UNHANDLED_EXCEPTION);
		httpServletResponse.setCommitted(true);

		authenticationExceptionHandler.handle(httpServletResponse, exception);

		assertThat(httpServletResponse.getStatus()).isEqualTo(200);
	}
}
