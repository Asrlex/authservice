package dev.api.auth.authservice.common.advice;

import dev.api.auth.authservice.common.filter.ApiEnvelope;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Set;

/**
 * Wraps API responses in a standard envelope unless the controller or method is annotated with {@link SkipResponseFormat}.
 */
@RestControllerAdvice
public class ApiResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

	private static final Set<Class<?>> PASS_THROUGH = Set.of(
			byte[].class, org.springframework.core.io.Resource.class, org.springframework.http.ProblemDetail.class
	);

	private final HttpServletRequest request;

	public ApiResponseWrapperAdvice(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Determines if the response should be wrapped.
	 * Skips wrapping if the controller class or method is annotated with {@link SkipResponseFormat}.
	 */
	@Override
	public boolean supports(MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		Object handler = returnType.getExecutable();
		if (returnType.getContainingClass().isAnnotationPresent(SkipResponseFormat.class)) return false;
		return !returnType.hasMethodAnnotation(SkipResponseFormat.class);
	}

	/**
	 * Wraps the response body in an {@link ApiEnvelope} if it's not already wrapped or of a pass-through type.
	 */
	@Override
	public Object beforeBodyWrite(@Nullable Object body,
								  @NonNull MethodParameter returnType,
								  @NonNull MediaType selectedContentType,
								  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
								  @NonNull ServerHttpRequest req,
								  @NonNull ServerHttpResponse res) {
		if (body instanceof ApiEnvelope<?> || body instanceof ResponseEntity<?>)
			return body;

		if (body != null && PASS_THROUGH.contains(body.getClass()))
			return body;

		String path = request.getRequestURI();
		return ApiEnvelope.ok(body, path);
	}
}

