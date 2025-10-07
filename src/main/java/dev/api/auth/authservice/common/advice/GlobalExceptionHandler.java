package dev.api.auth.authservice.common.advice;

import dev.api.auth.authservice.common.entities.ApiEnvelopeResponseCode;
import dev.api.auth.authservice.common.exceptions.ApiException;
import dev.api.auth.authservice.common.exceptions.RateLimitExceededException;
import dev.api.auth.authservice.common.exceptions.ResourceNotFoundException;
import dev.api.auth.authservice.common.filter.ApiEnvelope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/** Handle custom ApiException. */
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiEnvelope<Void>> handleApiException(ApiException ex, HttpServletRequest req) {
		return ResponseEntity
				.status(ex.getStatus())
				.body(ApiEnvelope.error(ex.getCode().name(), ex.getMessage(), req.getRequestURI()));
	}

	/** Handle validation errors. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiEnvelope<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		Map<String, Object> meta = new LinkedHashMap<>();
		meta.put("fields", ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> {
					assert fe.getDefaultMessage() != null;
					return Map.of("field", fe.getField(), "message", fe.getDefaultMessage());
				})
				.collect(Collectors.toList()));
		return ResponseEntity
				.badRequest()
				.body(ApiEnvelope.error(
						ApiEnvelopeResponseCode.VALIDATION_ERROR.name(),
						"Validation failed",
						req.getRequestURI(),
						meta));
	}

	/** Handle constraint violations. */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiEnvelope<Void>> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
		Map<String, Object> meta = Map.of("violations", ex.getConstraintViolations().stream()
				.map(v ->
						Map.of("property", v.getPropertyPath().toString(), "message", v.getMessage()))
				.toList());
		return ResponseEntity
				.badRequest()
				.body(ApiEnvelope.error(
						ApiEnvelopeResponseCode.VALIDATION_ERROR.name(),
						"Constraint violation",
						req.getRequestURI(),
						meta));
	}

	/** Handle bad requests. */
	@ExceptionHandler({
			MethodArgumentTypeMismatchException.class,
			HttpMessageNotReadableException .class
	})
	public ResponseEntity<ApiEnvelope<Void>> handleBadRequest(Exception ex, HttpServletRequest req) {
		return ResponseEntity
				.badRequest()
				.body(ApiEnvelope.error(ApiEnvelopeResponseCode.BAD_REQUEST.name(), ex.getMessage(), req.getRequestURI()));
	}

	/** Handle 404 not found. */
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiEnvelope<Void>> handleNotFound(NoHandlerFoundException ex, HttpServletRequest req) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(ApiEnvelope.error(ApiEnvelopeResponseCode.NOT_FOUND.name(), "Route not found", req.getRequestURI()));
	}

	/** Handle ResourceNotFoundException. */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiEnvelope<Void>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
		Map<String, Object> meta = ex.getEntityId() != null
				? Map.of("entityId", ex.getEntityId())
				: Map.of();
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(ApiEnvelope.error(
						ApiEnvelopeResponseCode.NOT_FOUND.name(),
						ex.getMessage(),
						req.getRequestURI(),
						meta));
	}

	@ExceptionHandler(RateLimitExceededException.class)
	public ResponseEntity<ApiEnvelope<Void>> handleRateLimitExceeded(RateLimitExceededException ex, HttpServletRequest req) {
		return ResponseEntity
				.status(HttpStatus.TOO_MANY_REQUESTS)
				.body(ApiEnvelope.error(
						ApiEnvelopeResponseCode.TOO_MANY_REQUESTS.name(),
						ex.getMessage(),
						req.getRequestURI()));
	}

	/** Handle ErrorResponseException for other HTTP status codes. */
	@ExceptionHandler(ErrorResponseException.class)
	public ResponseEntity<ApiEnvelope<Void>> handleErrorResponse(ErrorResponseException ex, HttpServletRequest req) {
		HttpStatusCode status = ex.getStatusCode();
		String code = switch (status.value()) {
			case 401 -> ApiEnvelopeResponseCode.UNAUTHORIZED.name();
			case 403 -> ApiEnvelopeResponseCode.FORBIDDEN.name();
			case 404 -> ApiEnvelopeResponseCode.NOT_FOUND.name();
			case 409 -> ApiEnvelopeResponseCode.CONFLICT.name();
			default -> ApiEnvelopeResponseCode.BAD_REQUEST.name();
		};
		return ResponseEntity
				.status(status)
				.body(ApiEnvelope.error(code, ex.getCause() != null ? ex.getCause().getMessage() : "Error", req.getRequestURI()));
	}

	/** Handle any other exceptions. */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiEnvelope<Void>> handleAny(Exception ex, HttpServletRequest req) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiEnvelope.error(ApiEnvelopeResponseCode.INTERNAL_ERROR.name(), "Unexpected error", req.getRequestURI(),
						Map.of("detail", ex.getClass().getSimpleName())));
	}
}
