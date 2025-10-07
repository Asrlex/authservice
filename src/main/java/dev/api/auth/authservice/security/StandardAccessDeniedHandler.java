package dev.api.auth.authservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.api.auth.authservice.common.entities.ApiEnvelopeResponseCode;
import dev.api.auth.authservice.common.filter.ApiEnvelope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StandardAccessDeniedHandler implements AccessDeniedHandler {
	private final ObjectMapper om;

	public StandardAccessDeniedHandler(ObjectMapper om) {
		this.om = om;
	}

	/**
	 * This is invoked when user tries to access a secured REST resource without supplying any credentials.
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
			throws IOException {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType("application/json");
		var body = ApiEnvelope.error(ApiEnvelopeResponseCode.FORBIDDEN.name(),
				ex.getMessage() != null ? ex.getMessage() : "Forbidden",
				request.getRequestURI());
		om.writeValue(response.getOutputStream(), body);
	}
}

