package dev.api.auth.authservice.api.users;

import dev.api.auth.authservice.api.users.dtos.UserDto;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GraphQL controller for user-related queries.
 */
@Component
public class UserGraphQLController {

	private final UserService userService;

	public UserGraphQLController(UserService userService) {
		this.userService = userService;
	}

	@QueryMapping
	public UserDto user(@Argument Long id) {
		return userService.findById(id);
	}

	@QueryMapping
	public List<UserDto> allUsers() {
		return userService.findAll();
	}

	@QueryMapping
	public UserDto me(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("Not authenticated");
		}
		return userService.getCurrent(authentication);
	}
}

