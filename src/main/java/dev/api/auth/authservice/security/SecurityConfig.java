package dev.api.auth.authservice.security;

import dev.api.auth.authservice.common.filter.AuditContextFilter;
import dev.api.auth.authservice.common.filter.RequestLoggingFilter;
import dev.api.auth.authservice.security.guards.ApiKeyAuthenticationFilter;
import dev.api.auth.authservice.security.guards.CompositeAuthenticationFilter;
import dev.api.auth.authservice.security.guards.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for setting up authentication and authorization.
 */
@Configuration
public class SecurityConfig {

	private final StandardAuthEntryPoint entryPoint;
	private final StandardAccessDeniedHandler deniedHandler;

	public SecurityConfig(StandardAuthEntryPoint entryPoint,
						  StandardAccessDeniedHandler deniedHandler) {
		this.entryPoint = entryPoint;
		this.deniedHandler = deniedHandler;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
										   CompositeAuthenticationFilter compositeAuthenticationFilter) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/auth/**",
								"/healthcheck",
								"/v3/api-docs/**",
								"/api-docs/**",
								"/api-docs.yaml",
								"/swagger-api-docs",
								"/swagger-ui.html",
								"/swagger-ui/**"
						).permitAll()
						.anyRequest().authenticated()
				)
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(entryPoint)
						.accessDeniedHandler(deniedHandler)
				)
				.addFilterBefore(requestLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(compositeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterAfter(auditContextFilter(), UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuditContextFilter auditContextFilter() {
		return new AuditContextFilter();
	}

	@Bean
	public RequestLoggingFilter requestLoggingFilter() {
		return new RequestLoggingFilter();
	}

	@Bean
	public CompositeAuthenticationFilter compositeAuthenticationFilter(
			JwtAuthenticationFilter jwtAuthenticationFilter,
			ApiKeyAuthenticationFilter apiKeyAuthenticationFilter) {
		return new CompositeAuthenticationFilter(jwtAuthenticationFilter, apiKeyAuthenticationFilter);
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
		return new JwtAuthenticationFilter(jwtService);
	}

	@Bean
	public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter(@Value("${x-api-key}") String expectedApiKey) {
		return new ApiKeyAuthenticationFilter(expectedApiKey);
	}
}