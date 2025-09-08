package com.sillim.recordit.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.global.dto.response.ErrorResponse;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationExceptionHandler {

	private final ObjectMapper objectMapper;

	public void handle(HttpServletResponse response, ApplicationException exception) throws IOException {
		if (response.isCommitted()) {
			return;
		}
		ErrorResponse body = ErrorResponse.from(exception.getErrorCode());
		writeUnauthorizedResponse(response, body);
	}

	public void handle(HttpServletResponse response, AuthenticationException exception) throws IOException {
		if (response.isCommitted()) {
			return;
		}
		ErrorResponse body = ErrorResponse.from(resolveErrorCode(exception));
		writeUnauthorizedResponse(response, body);
	}

	private void writeUnauthorizedResponse(HttpServletResponse response, ErrorResponse body) throws IOException {
		log.info("Authentication Exception: {} {}", body.errorCode(), body.message());

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		response.getWriter().write(
				objectMapper.writeValueAsString(ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(body)));
		response.getWriter().flush();
	}

	private ErrorCode resolveErrorCode(AuthenticationException e) {
		log.debug("Implementation of AuthenticationException is {}", e.getClass().getSimpleName());
		if (e instanceof AuthenticationCredentialsNotFoundException
				|| e instanceof InsufficientAuthenticationException) {
			return ErrorCode.AUTHENTICATION_REQUIRED;
		} else {
			return ErrorCode.AUTHENTICATION_FAILED;
		}
	}
}
