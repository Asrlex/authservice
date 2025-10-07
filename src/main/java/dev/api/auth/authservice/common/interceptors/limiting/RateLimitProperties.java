package dev.api.auth.authservice.common.interceptors.limiting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitProperties {
	private Integer admin;
	private Integer user;
	private Integer api;
	private Integer guest;
	private Integer window;
}

