package com.sillim.recordit.config.security.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

class AuthenticationExceptionHandlerTest {

	AuthenticationExceptionHandler authenticationExceptionHandler =
			new AuthenticationExceptionHandler(new ObjectMapper());

	@Test
	@DisplayName("exception을 response를 통해 출력한다.")
	void responseException() throws IOException {
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		ApplicationException exception = new ApplicationException(ErrorCode.UNHANDLED_EXCEPTION);

		authenticationExceptionHandler.handle(httpServletResponse, exception);

		assertThat(httpServletResponse.getStatus()).isEqualTo(401);
		assertThat(httpServletResponse.getContentType())
				.isEqualTo("application/json;charset=UTF-8");
		assertThat(httpServletResponse.getCharacterEncoding()).isEqualTo("UTF-8");
	}

	@Test
	@DisplayName("response가 commit되어 있다면 출력되지 않는다.")
	void notWriteIfResponseCommitted() throws IOException {
		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
		ApplicationException exception = new ApplicationException(ErrorCode.UNHANDLED_EXCEPTION);
		httpServletResponse.setCommitted(true);

		authenticationExceptionHandler.handle(httpServletResponse, exception);

		assertThat(httpServletResponse.getStatus()).isEqualTo(200);
	}
}
