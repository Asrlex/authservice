package dev.api.auth.authservice.common.audit;

import dev.api.auth.authservice.common.entities.StandardParameters;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;

public class AuditListener {
	@PrePersist
	public void setInitialFields(AuditableEntity entity) {
		Instant now = Instant.now();
		entity.setCreatedAt(now);
		entity.setUpdatedAt(now);
		entity.setCreatedBy(AuditContext.getActor().orElse(StandardParameters.SYSTEM_USER));
		entity.setUpdatedBy(entity.getCreatedBy());
	}

	@PreUpdate
	public void setUpdateFields(AuditableEntity entity) {
		entity.setUpdatedAt(Instant.now());
		entity.setUpdatedBy(AuditContext.getActor().orElse(StandardParameters.SYSTEM_USER));
	}
}
