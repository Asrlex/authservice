package dev.api.auth.authservice.security.guards;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * CompositeAuthenticationFilter tries JWT authentication first, then API key authentication if JWT fails.
 */
public class CompositeAuthenticationFilter extends OncePerRequestFilter {

	private final JwtAuthenticationFilter jwtFilter;
	private final ApiKeyAuthenticationFilter apiKeyFilter;

	public CompositeAuthenticationFilter(JwtAuthenticationFilter jwtFilter, ApiKeyAuthenticationFilter apiKeyFilter) {
		this.jwtFilter = jwtFilter;
		this.apiKeyFilter = apiKeyFilter;
	}

	/**
	 * First tries JWT authentication. If it fails (no authentication set), tries API key authentication.
	 */
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
									@NonNull HttpServletResponse response,
									@NonNull FilterChain chain)
			throws ServletException, IOException {

		jwtFilter.doFilter(request, response, (req, res) -> {
			if (SecurityContextHolder.getContext().getAuthentication() == null) {
				apiKeyFilter.doFilter(req, res, chain);
			} else {
				chain.doFilter(req, res);
			}
		});
	}
}
