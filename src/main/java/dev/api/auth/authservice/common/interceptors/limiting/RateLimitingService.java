package dev.api.auth.authservice.common.interceptors.limiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingService {

	private final RateLimitProperties props;
	private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

	public RateLimitingService(RateLimitProperties props) {
		this.props = props;
	}

	public Bucket resolveBucket(String key) {
		return cache.computeIfAbsent(key, this::createNewBucket);
	}

	private Bucket createNewBucket(String key) {
		String role = key.contains(":") ? key.split(":")[1] : "ROLE_GUEST";

		int limit = switch (role) {
			case "ROLE_ADMIN" -> props.getAdmin();
			case "ROLE_USER"  -> props.getUser();
			case "ROLE_API"   -> props.getApi();
			default           -> props.getGuest();
		};

		return Bucket.builder()
				.addLimit(Bandwidth.simple(limit, Duration.ofSeconds(props.getWindow())))
				.build();
	}
}
