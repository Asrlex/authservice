package dev.api.auth.authservice.api.users;

import dev.api.auth.authservice.api.users.dtos.PasswordChange;
import dev.api.auth.authservice.api.users.dtos.UpdateUserDto;
import dev.api.auth.authservice.api.users.dtos.UserDto;
import dev.api.auth.authservice.common.entities.search.SearchCriteria;
import dev.api.auth.authservice.common.entities.search.SqlParameters;
import dev.api.auth.authservice.common.exceptions.ResourceNotFoundException;
import dev.api.auth.authservice.common.kafka.events.users.UserDeletedEvent;
import dev.api.auth.authservice.common.kafka.events.users.UserRestoredEvent;
import dev.api.auth.authservice.common.kafka.events.users.UserUpdatedEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;

	public UserService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			ApplicationEventPublisher eventPublisher) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Gets all users
	 *
	 * @return List<User> - all users
	 */
	public List<UserDto> list(SearchCriteria searchCriteria) {
		return this.userRepository.findAll().stream()
				.filter(user -> {
					if (searchCriteria.getFilters() != null) {
						for (SearchCriteria.Filter filter : searchCriteria.getFilters()) {
							boolean like = Objects.equals(filter.operator().toString(), SqlParameters.SqlOperator.LIKE);
							if (filter.field().equals("username") && like) {
								String value = (String) filter.value();
								if (!user.getUsername().contains(value)) {
									return false;
								}
							}
							if (filter.field().equals("email") && like) {
								String value = (String) filter.value();
								if (!user.getEmail().contains(value)) {
									return false;
								}
							}
						}
					}
					return true;
				})
				.map(User::generateDto)
				.sorted((u1, u2) -> {
					if (searchCriteria.getSorters() != null) {
						for (SearchCriteria.Sorter sorter : searchCriteria.getSorters()) {
							boolean asc = Objects.equals(sorter.operator().toString(), SqlParameters.SqlOrder.ASC);
							if (sorter.field().equals("username")) {
								if (asc) {
									return u1.getUsername().compareTo(u2.getUsername());
								} else {
									return u2.getUsername().compareTo(u1.getUsername());
								}
							}
							if (sorter.field().equals("email")) {
								if (asc) {
									return u1.getEmail().compareTo(u2.getEmail());
								} else {
									return u2.getEmail().compareTo(u1.getEmail());
								}
							}
						}
					}
					return u1.getId().compareTo(u2.getId());
				})
				.skip(searchCriteria.getPage() != null && searchCriteria.getPageSize() != null
						? (long) (searchCriteria.getPage() - 1) * searchCriteria.getPageSize()
						: 0L)
				.limit(searchCriteria.getPageSize() != null ? searchCriteria.getPageSize() : Long.MAX_VALUE)
				.toList();
	}

	/**
	 * Gets all users
	 *
	 * @return List<User> - all users
	 */
	@Cacheable(value = "users", key = "'all'")
	public List<UserDto> findAll() {
		return this.userRepository.findAll().stream()
				.map(User::generateDto)
				.sorted(Comparator.comparing(UserDto::getId))
				.toList();
	}

	/**
	 * Gets all users including deleted ones
	 *
	 * @return List<User> - all users including deleted ones
	 */
	@Cacheable(value = "users", key = "'allIncludingDeleted'")
	public List<UserDto> findAllIncludingDeleted() {
		return this.userRepository.findAllIncludingDeleted().stream()
				.map(User::generateDto)
				.sorted(Comparator.comparing(UserDto::getId))
				.toList();
	}

	/**
	 * Get the current authenticated user
	 *
	 * @param auth - Authentication object
	 * @return User - current user
	 */
	public UserDto getCurrent(Authentication auth) {
		String email = auth.getName();
		return findByEmail(email);
	}

	/**
	 * Get a user by its ID
	 *
	 * @param id - the user's ID
	 * @return User - requested user
	 */
	@Cacheable(value = "users", key = "#id")
	public UserDto findById(Long id) {
		Optional<User> requestedUser = this.userRepository.findById(id);
		if (requestedUser.isPresent()) {
			User user = requestedUser.get();
			return user.generateDto();
		} else {
			throw new ResourceNotFoundException("User with id " + id + " not found");
		}
	}

	/**
	 * Get a user by its ID including deleted ones
	 *
	 * @param id - the user's ID
	 * @return User - requested user
	 */
	@Cacheable(value = "users", key = "#id")
	public UserDto findByIdIncludingDeleted(Long id) {
		Optional<User> requestedUser = this.userRepository.findByIdIncludingDeleted(id).stream().findFirst();
		if (requestedUser.isPresent()) {
			User user = requestedUser.get();
			return user.generateDto();
		} else {
			throw new ResourceNotFoundException("User with id " + id + " not found");
		}
	}

	/**
	 * Get a user by its email
	 *
	 * @param email - the user's email
	 * @return User - requested user
	 */
	public UserDto findByEmail(String email) {
		Optional<User> requestedUser = this.userRepository.findByEmail(email);
		if (requestedUser.isPresent()) {
			User user = requestedUser.get();
			return user.generateDto();
		} else {
			throw new ResourceNotFoundException("User with email " + email + " not found");
		}
	}

	/**
	 * Get a user by its username
	 *
	 * @param username - the user's username
	 * @return User - requested user
	 */
	public UserDto findByUsername(String username) {
		Optional<User> requestedUser = this.userRepository.findByUsername(username);
		if (requestedUser.isPresent()) {
			User user = requestedUser.get();
			return user.generateDto();
		} else {
			throw new ResourceNotFoundException("User with username " + username + " not found");
		}
	}

	/**
	 * Update an existing User
	 *
	 * @param dto - User objet to be updated
	 * @return User - updated User
	 */
	@Transactional
	@Caching(evict = {
			@CacheEvict(value = "users", key = "'all'"),
			@CacheEvict(value = "users", key = "'allIncludingDeleted'"),
			@CacheEvict(value = "users", key = "#dto.id()")
	})
	public UserDto update(UpdateUserDto dto) {
		if (this.userRepository.existsById(dto.id())) {
			User updated = this.userRepository.save(dto.updateUser());
			UserDto updatedDto = updated.generateDto();
			eventPublisher.publishEvent(new UserUpdatedEvent(updatedDto));
			return updatedDto;
		} else {
			throw new ResourceNotFoundException("User with id " + dto.id() + " not found");
		}
	}

	/**
	 * Change password for authenticated user
	 *
	 * @param dto - map containing old and new password
	 * @return map containing success message
	 */
	@Transactional
	public UserDto passwordChange(PasswordChange dto, Authentication auth) {
		String email = auth.getName();

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));

		if (!passwordEncoder.matches(dto.currentPassword(), user.getPasswordHash())) {
			throw new RuntimeException("Invalid current password");
		}

		user.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
		User saved = userRepository.save(user);
		UserDto savedDto = saved.generateDto();
		eventPublisher.publishEvent(new UserUpdatedEvent(savedDto));

		return savedDto;
	}

	/**
	 * Admin reset password for a user
	 *
	 * @param dto - map containing userId and new password
	 * @return User - updated User
	 */
	@Transactional
	public UserDto adminPasswordChange(PasswordChange dto) {
		User user = userRepository.findById(dto.userId())
				.orElseThrow(() -> new ResourceNotFoundException("User with id " + dto.userId() + " not found"));
		user.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
		User saved = userRepository.save(user);
		UserDto savedDto = saved.generateDto();
		eventPublisher.publishEvent(new UserUpdatedEvent(savedDto));

		return savedDto;
	}

	/**
	 * Delete a user by its ID
	 *
	 * @param id - the user's ID
	 */
	@Transactional
	@Caching(evict = {
			@CacheEvict(value = "users", key = "'all'"),
			@CacheEvict(value = "users", key = "'allIncludingDeleted'"),
			@CacheEvict(value = "users", key = "#id")
	})
	public void delete(Long id) {
		this.userRepository.delete(
				this.userRepository.findById(id).orElseThrow(() ->
						new ResourceNotFoundException("User with id " + id + " not found")
				)
		);
		eventPublisher.publishEvent(new UserDeletedEvent(id));
	}

	/**
	 * Restore a soft-deleted user by its ID
	 *
	 * @param id - the user's ID
	 * @return User - restored user
	 */
	@Transactional
	@Caching(evict = {
			@CacheEvict(value = "users", key = "'all'"),
			@CacheEvict(value = "users", key = "'allIncludingDeleted'"),
			@CacheEvict(value = "users", key = "#id")
	})
	public UserDto restoreById(Long id) {
		Optional<User> originalUser = this.userRepository.findByIdIncludingDeleted(id).stream().findFirst();
		if (originalUser.isEmpty()) {
			throw new ResourceNotFoundException("User with id " + id + " not found or not deleted");
		}
		this.userRepository.restoreById(originalUser.get().getId());
		UserDto restoredUser = originalUser.get().generateDto();
		eventPublisher.publishEvent(new UserRestoredEvent(restoredUser));
		return restoredUser;
	}
}
