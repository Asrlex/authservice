package dev.api.auth.authservice.api.auth;

import dev.api.auth.authservice.api.auth.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
	Optional<RefreshToken> findByJti(UUID jti);
	Optional<RefreshToken> findByTokenHash(String tokenHash);
	List<RefreshToken> findAllByUserIdAndRevokedFalseAndExpiresAtAfter(String userId, Instant now);
	@Modifying
	@Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.userEmail = :userId")
	void revokeAllByUserId(@Param("userId") String userId);
	void deleteAllByRevokedTrueOrExpiresAtBefore(Instant now);
}

