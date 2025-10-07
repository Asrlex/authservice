package dev.api.auth.authservice.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.api.auth.authservice.common.audit.AuditContext;
import dev.api.auth.authservice.common.entities.StandardParameters;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Filter for logging web requests and responses.
 */
public class RequestLoggingFilter extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Logs request and response details including method, URI, status, lengths, and processing time.
	 * Also assigns a unique request ID for tracing.
	 * @param request - the HTTP request
	 * @param response - the HTTP response
	 * @param chain - the filter chain
	 * @throws ServletException - if a servlet error occurs
	 * @throws IOException - if an I/O error occurs
	 */
	@Override
	protected void doFilterInternal(@Nonnull HttpServletRequest request,
									@Nonnull HttpServletResponse response,
									FilterChain chain)
			throws ServletException, IOException {

		String requestId = UUID.randomUUID().toString();
		MDC.put("requestId", requestId);
		long start = System.currentTimeMillis();
		String actor = AuditContext.getActor().orElse(StandardParameters.SYSTEM_USER);
		MDC.put("actor", actor);

		ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

		try {
			response.addHeader("X-Request-Id", requestId);
			chain.doFilter(req, res);
		} finally {
			long duration = System.currentTimeMillis() - start;

			Map<String, Object> logEntry = new HashMap<>();
			logEntry.put("timestamp", Instant.now().toString());
			logEntry.put("requestId", requestId);
			logEntry.put("method", request.getMethod());
			logEntry.put("uri", request.getRequestURI());
			logEntry.put("status", response.getStatus());
			logEntry.put("durationMs", duration);
			logEntry.put("clientIp", request.getRemoteAddr());
			logEntry.put("userAgent", request.getHeader("User-Agent"));
			logEntry.put("actor", actor);
			String queryString = request.getQueryString();
			logEntry.put("queryString", queryString);
			Map<String, String[]> parameterMap = request.getParameterMap();
			Map<String, Object> params = new HashMap<>();
			parameterMap.forEach((k, v) -> params.put(k, v.length == 1 ? v[0] : v));
			logEntry.put("parameters", params);

			String jsonLog = objectMapper.writeValueAsString(logEntry);
			log.info(jsonLog);

			res.copyBodyToResponse();

			MDC.clear();
		}
	}
}
