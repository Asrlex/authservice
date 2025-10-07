package dev.api.auth.authservice.common.exceptions;

import dev.api.auth.authservice.common.entities.ApiEnvelopeResponseCode;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
	private final HttpStatus status;
	private final ApiEnvelopeResponseCode code;

	public ApiException(HttpStatus status, ApiEnvelopeResponseCode code, String message) {
		super(message);
		this.status = status;
		this.code = code;
	}

	public HttpStatus getStatus() { return status; }
	public ApiEnvelopeResponseCode getCode() { return code; }

	public static ApiException unauthorized(String msg) {
		return new ApiException(HttpStatus.UNAUTHORIZED, ApiEnvelopeResponseCode.UNAUTHORIZED, msg);
	}
	public static ApiException forbidden(String msg) {
		return new ApiException(HttpStatus.FORBIDDEN, ApiEnvelopeResponseCode.FORBIDDEN, msg);
	}
	public static ApiException notFound(String msg) {
		return new ApiException(HttpStatus.NOT_FOUND, ApiEnvelopeResponseCode.NOT_FOUND, msg);
	}
	public static ApiException badRequest(String msg) {
		return new ApiException(HttpStatus.BAD_REQUEST, ApiEnvelopeResponseCode.BAD_REQUEST, msg);
	}
}
