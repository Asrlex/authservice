package dev.api.auth.authservice.security.filters;

import dev.api.auth.authservice.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter that validates JWT tokens in the Authorization header.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	/**
	 * Extracts and validates the JWT token from the Authorization header.
	 * If valid, sets the authentication in the SecurityContext.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
			throws ServletException, IOException {

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				jwtService.validateToken(token);
				Jws<Claims> jwsClaims = jwtService.parseToken(token);
				String subject = jwsClaims.getBody().getSubject();
				Claims claims = jwsClaims.getBody();
				String roleFromClaims = claims.get("role", String.class);
				var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleFromClaims));
				var auth = new UsernamePasswordAuthenticationToken(subject, null, authorities);
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (Exception ignored) {
				SecurityContextHolder.clearContext();
			}
		}

		chain.doFilter(request, response);
	}
}
