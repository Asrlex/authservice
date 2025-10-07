package dev.api.auth.authservice.api.auth;

import dev.api.auth.authservice.api.auth.entities.IssuedTokens;
import dev.api.auth.authservice.api.auth.entities.RefreshToken;
import dev.api.auth.authservice.api.auth.utils.TokenUtils;
import dev.api.auth.authservice.common.exceptions.InvalidTokenException;
import dev.api.auth.authservice.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Getter
public class RefreshTokenService {

	private final RefreshTokenRepository repo;
	private final TokenUtils tokenUtils;
	private final JwtService jwtService;
	private final Duration refreshTtl;
	private final int tokenByteLen;

	public RefreshTokenService(RefreshTokenRepository repo,
							   TokenUtils tokenUtils,
							   JwtService jwtService,
							   @Value("${auth.refresh.ttl:2592000s}") Duration refreshTtl,
							   @Value("${auth.refresh.token-bytes:32}") int tokenByteLen) {
		this.repo = repo;
		this.tokenUtils = tokenUtils;
		this.jwtService = jwtService;
		this.refreshTtl = refreshTtl;
		this.tokenByteLen = tokenByteLen;
	}

	@Transactional
	public IssuedTokens createTokensForUser(String userId, Map<String,Object> accessClaims, String clientId, String ip, String userAgent) {
		String accessToken = jwtService.generateAccessToken(String.valueOf(userId), accessClaims);

		String rawRefresh = tokenUtils.generateRandomToken(tokenByteLen);
		String hash = tokenUtils.hashToken(rawRefresh);

		RefreshToken entity = new RefreshToken();
		entity.setUserEmail(userId);
		entity.setClientId(clientId);
		entity.setTokenHash(hash);
		entity.setExpiresAt(Instant.now().plus(refreshTtl));
		entity.setIpAddress(ip);
		entity.setUserAgent(userAgent);
		repo.save(entity);

		return new IssuedTokens(accessToken, rawRefresh, entity.getJti());
	}

	/**
	 * Refresh flow with rotation:
	 * - locate token by hash
	 * - check not revoked & not expired & optional client match
	 * - issue new access token + new refresh token
	 * - mark old token revoked and set replaced_by_jti
	 * - save new token and return it
	 */
	@Transactional
	public IssuedTokens refresh(String presentedRefreshToken, String clientId, Map<String,Object> accessClaims) {
		String hash = tokenUtils.hashToken(presentedRefreshToken);

		RefreshToken old = repo.findByTokenHash(hash)
				.orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

		if (old.isRevoked() || old.getExpiresAt().isBefore(Instant.now())) {
			repo.revokeAllByUserId(old.getUserEmail());
			throw new InvalidTokenException("Refresh token expired or revoked");
		}

		if (clientId != null && !Objects.equals(clientId, old.getClientId())) {
			throw new InvalidTokenException("Client mismatch");
		}

		String newRaw = tokenUtils.generateRandomToken(tokenByteLen);
		String newHash = tokenUtils.hashToken(newRaw);
		RefreshToken newToken = new RefreshToken();
		newToken.setUserEmail(old.getUserEmail());
		newToken.setClientId(clientId);
		newToken.setTokenHash(newHash);
		newToken.setExpiresAt(Instant.now().plus(refreshTtl));
		newToken.setIpAddress(null);
		newToken.setUserAgent(null);
		repo.save(newToken);

		old.setRevoked(true);
		old.setReplacedByJti(newToken.getJti());
		old.setLastUsedAt(Instant.now());
		repo.save(old);

		String newAccess = jwtService.generateAccessToken(String.valueOf(old.getUserEmail()), accessClaims);
		return new IssuedTokens(newAccess, newRaw, newToken.getJti());
	}

	@Transactional
	public void revokeByJti(UUID jti) {
		RefreshToken t = repo.findByJti(jti).orElseThrow(() -> new InvalidTokenException("Refresh token not found"));
		t.setRevoked(true);
		repo.save(t);
	}

	@Transactional
	public void revokeAllForUser(String userId) {
		repo.revokeAllByUserId(userId);
	}
}

