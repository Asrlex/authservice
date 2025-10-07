package dev.api.auth.authservice.common.cron;

import dev.api.auth.authservice.api.auth.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
public class RefreshTokenCleanupJob {

	private static final Logger log = LoggerFactory.getLogger(RefreshTokenCleanupJob.class);
	private final RefreshTokenRepository refreshTokenRepository;

	public RefreshTokenCleanupJob(RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void run() {
		log.info("Running RefreshTokenCleanupJob at {}", LocalDateTime.now());
		refreshTokenRepository.deleteAllByRevokedTrueOrExpiresAtBefore(Instant.now());
	}
}
