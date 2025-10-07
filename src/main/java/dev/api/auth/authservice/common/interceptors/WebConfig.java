package dev.api.auth.authservice.common.interceptors;

import dev.api.auth.authservice.common.interceptors.limiting.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration to register interceptors.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final RateLimitInterceptor rateLimitInterceptor;

	public WebConfig(RateLimitInterceptor rateLimitInterceptor) {
		this.rateLimitInterceptor = rateLimitInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(rateLimitInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns(
						"/auth/**",
						"/healthcheck",
						"/swagger-ui/**",
						"/v3/api-docs/**"
				);
	}
}

