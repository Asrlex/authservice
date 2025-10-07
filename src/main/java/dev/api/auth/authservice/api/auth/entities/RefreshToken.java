package dev.api.auth.authservice.api.auth.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_refresh_tokens")
@Getter @Setter
public class RefreshToken {

	@Id
	@Column(nullable=false, updatable=false)
	private UUID id = UUID.randomUUID();

	@Column(nullable=false, unique=true)
	private UUID jti = UUID.randomUUID();

	@Column(name="user_id", nullable=false)
	private String userEmail;

	@Column(name="client_id")
	private String clientId;

	@Column(name="token_hash", nullable=false, length = 128)
	private String tokenHash; // sha256 hex

	@Column(name="created_at", nullable=false)
	private Instant createdAt = Instant.now();

	@Column(name="last_used_at")
	private Instant lastUsedAt;

	@Column(name="expires_at", nullable=false)
	private Instant expiresAt;

	@Column(nullable=false)
	private boolean revoked = false;

	@Column(name="replaced_by_jti")
	private UUID replacedByJti;

	@Column(name="ip_address")
	private String ipAddress;

	@Column(name="user_agent")
	private String userAgent;
}
