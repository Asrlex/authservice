package dev.api.auth.authservice.common.audit;

import dev.api.auth.authservice.common.entities.StandardParameters;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import java.time.Instant;
import java.util.Optional;

@MappedSuperclass
@EntityListeners(AuditListener.class)
public abstract class AuditableEntity implements IAuditable<String, Instant> {

	@Column(name = "created_at", nullable = false, updatable = false)
	@Convert(converter = InstantLongConverter.class)
	private Instant createdAt;

	@Column(name = "created_by", nullable = false, updatable = false)
	private String createdBy = StandardParameters.SYSTEM_USER;

	@Column(name = "updated_at", nullable = false)
	@Convert(converter = InstantLongConverter.class)
	private Instant updatedAt;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "deleted_at")
	@Convert(converter = InstantLongConverter.class)
	private Instant deletedAt;

	@Column(name = "deleted_by")
	private String deletedBy;

	@Override
	public String getCreatedBy() { return createdBy; }
	@Override
	public void setCreatedBy(String created_by) { this.createdBy = created_by; }

	@Override
	public Instant getCreatedAt() { return createdAt; }
	@Override
	public void setCreatedAt(Instant created_at) { this.createdAt = created_at; }

	@Override
	public Optional<String> getUpdatedBy() { return Optional.ofNullable(updatedBy); }
	@Override
	public void setUpdatedBy(String updated_by) { this.updatedBy = updated_by; }

	@Override
	public Optional<Instant> getUpdatedAt() { return Optional.ofNullable(updatedAt); }
	@Override
	public void setUpdatedAt(Instant updated_at) { this.updatedAt = updated_at; }

	@Override
	public Optional<String> getDeletedBy() { return Optional.ofNullable(deletedBy); }
	@Override
	public void setDeletedBy(String deleted_by) { this.deletedBy = deleted_by; }

	@Override
	public Optional<Instant> getDeletedAt() { return Optional.ofNullable(deletedAt); }
	@Override
	public void setDeletedAt(Instant deleted_at) { this.deletedAt = deleted_at; }
}

