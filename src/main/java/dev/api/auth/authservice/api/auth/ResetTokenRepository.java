package dev.api.auth.authservice.api.auth;

import dev.api.auth.authservice.api.auth.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
	Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
