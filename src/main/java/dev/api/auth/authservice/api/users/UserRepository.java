package dev.api.auth.authservice.api.users;


import dev.api.auth.authservice.common.audit.AuditRepository;

import java.util.Optional;

public interface UserRepository extends AuditRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByUsername(String username);
}
