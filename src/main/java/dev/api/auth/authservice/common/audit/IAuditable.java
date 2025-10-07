package dev.api.auth.authservice.common.audit;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;

public interface IAuditable<U, T extends TemporalAccessor> {

	U getCreatedBy();
	void setCreatedBy(U createdBy);

	T getCreatedAt();
	void setCreatedAt(T createdAt);

	Optional<U> getUpdatedBy();
	void setUpdatedBy(U updatedBy);

	Optional<T> getUpdatedAt();
	void setUpdatedAt(T updatedAt);

	Optional<U> getDeletedBy();
	void setDeletedBy(U deletedBy);

	Optional<T> getDeletedAt();
	void setDeletedAt(T deletedAt);
}
