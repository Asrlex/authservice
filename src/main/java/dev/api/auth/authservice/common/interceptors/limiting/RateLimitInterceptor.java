package dev.api.auth.authservice.common.interceptors.limiting;

import dev.api.auth.authservice.common.exceptions.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
	private final RateLimitingService rateLimitingService;

	public RateLimitInterceptor(RateLimitingService rateLimitingService) {
		this.rateLimitingService = rateLimitingService;
	}

	@Override
	public boolean preHandle(@Nonnull HttpServletRequest request,
							 @Nonnull HttpServletResponse response,
							 @Nonnull Object handler) throws RateLimitExceededException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Bucket bucket = rateLimitingService.resolveBucket(
					(auth != null && auth.isAuthenticated())
							? auth.getName() + ":" + auth.getAuthorities().stream().findFirst().map(Object::toString).orElse("ROLE_GUEST")
							: "ROLE_GUEST"
			);
			if (bucket.tryConsume(1)) {
				return true;
			} else {
				throw new RateLimitExceededException("Rate limit exceeded for user: " +
						(auth != null ? auth.getName() : "GUEST"));
			}
	}
}
