package dev.api.auth.authservice.common.filter;

import java.time.Instant;
import java.util.Map;

/**
 * A standard API response envelope.
 * @param <T> the type of the data
 */
public record ApiEnvelope<T>(
		boolean success,
		String code,
		String message,
		T data,
		Map<String, Object> meta,
		String path,
		Instant timestamp
) {
	public static <T> ApiEnvelope<T> ok(T data, String path) {
		return new ApiEnvelope<>(true, "OK", null, data, null, path, Instant.now());
	}

	public static ApiEnvelope<Void> error(String code, String message, String path, Map<String,Object> meta) {
		return new ApiEnvelope<>(false, code, message, null, meta, path, Instant.now());
	}

	public static ApiEnvelope<Void> error(String code, String message, String path) {
		return error(code, message, path, null);
	}
}

