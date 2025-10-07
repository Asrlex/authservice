package dev.api.auth.authservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.api.auth.authservice.common.entities.ApiEnvelopeResponseCode;
import dev.api.auth.authservice.common.filter.ApiEnvelope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StandardAuthEntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper om;

	public StandardAuthEntryPoint(ObjectMapper om) {
		this.om = om;
	}

	/**
	 * This method is called whenever an exception is thrown due to an unauthenticated user trying to access a
	 * 	resource that requires authentication
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
			throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		var body = ApiEnvelope.error(ApiEnvelopeResponseCode.UNAUTHORIZED.name(),
				authEx.getMessage() != null ? authEx.getMessage() : "Unauthorized",
				request.getRequestURI());
		om.writeValue(response.getOutputStream(), body);
	}
}

