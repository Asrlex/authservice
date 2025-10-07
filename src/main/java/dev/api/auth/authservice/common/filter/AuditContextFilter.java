package dev.api.auth.authservice.common.filter;

import dev.api.auth.authservice.common.audit.AuditContext;
import dev.api.auth.authservice.common.entities.StandardParameters;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuditContextFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
									@NonNull HttpServletResponse response,
									@NonNull FilterChain filterChain)
			throws ServletException, IOException {

		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth.isAuthenticated()) {
				AuditContext.setActor(auth.getName());
			} else {
				AuditContext.setActor(StandardParameters.SYSTEM_USER);
			}

			filterChain.doFilter(request, response);
		} finally {
			AuditContext.clear();
		}
	}
}
