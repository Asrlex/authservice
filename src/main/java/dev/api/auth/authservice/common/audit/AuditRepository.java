package dev.api.auth.authservice.common.audit;

import dev.api.auth.authservice.api.users.User;
import dev.api.auth.authservice.common.entities.StandardParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface AuditRepository<T extends AuditableEntity, ID>
		extends JpaRepository<T, ID> {

	default void softDelete(T entity) {
		String actor = AuditContext.getActor().orElse(StandardParameters.SYSTEM_USER);
		entity.setDeletedAt(Instant.now());
		entity.setDeletedBy(actor);
		entity.setUpdatedAt(Instant.now());
		entity.setUpdatedBy(actor);
		save(entity);
	}

	@Override
	default void delete(@NonNull T entity) {
		softDelete(entity);
	}

	@Override
	default void deleteById(@NonNull ID id) {
		findById(id).ifPresent(this::delete);
	}

	@Override
	default void deleteAll(Iterable<? extends T> entities) {
		entities.forEach(this::delete);
	}

	@Query(value = BaseQueries.FIND_ALL_INCLUDE_DELETED)
	List<User> findAllIncludingDeleted();

	@Query(value = BaseQueries.FIND_BY_ID_INCLUDE_DELETED)
	List<T> findByIdIncludingDeleted(@Param("id") ID id);

	@Query(value = BaseQueries.RESTORE_BY_ID)
	Optional<T> restoreById(@Param("id") ID id);
}
