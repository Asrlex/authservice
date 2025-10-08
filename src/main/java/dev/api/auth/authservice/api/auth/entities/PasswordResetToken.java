package dev.api.auth.authservice.api.auth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
public class PasswordResetToken {
	@Id
	@GeneratedValue
	private Long id;

	@Column(name="user_id", nullable=false)
	private Long userId;

	@Column(name="token_hash", nullable=false, length = 128, unique=true)
	private String tokenHash;

	@Column(name="created_at", nullable=false)
	private boolean used = false;

	@Column(name="expires_at", nullable=false)
	private Instant expiresAt;

	public PasswordResetToken(Long userId, String tokenHash) {
		this.userId = userId;
		this.tokenHash = tokenHash;
		this.used = false;
		this.expiresAt = Instant.now().plus(30, ChronoUnit.MINUTES);
	}

	public PasswordResetToken() {}
}
