package dev.api.auth.authservice.security.guards;

import dev.api.auth.authservice.common.entities.StandardParameters;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter to authenticate requests based on an API key provided in the headers.
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

	private static final String API_KEY_HEADER = "X-api-key";
	private final String EXPECTED_API_KEY;

	public ApiKeyAuthenticationFilter(String expectedApiKey) {
		this.EXPECTED_API_KEY = expectedApiKey;
	}

	/**
	 * Checks for the API key in the request headers and sets the authentication context if valid.
	 */
	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain chain)
			throws ServletException, IOException {

		String apiKey = request.getHeader(API_KEY_HEADER);
		if (apiKey != null && apiKey.equals(EXPECTED_API_KEY)) {
			var authorities = List.of(new SimpleGrantedAuthority("ROLE_API"));
			var auth = new UsernamePasswordAuthenticationToken(StandardParameters.API_KEY_USER, null, authorities);
			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		chain.doFilter(request, response);
	}
}