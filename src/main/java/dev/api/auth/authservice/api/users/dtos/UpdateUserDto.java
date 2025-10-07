package dev.api.auth.authservice.api.users.dtos;

import dev.api.auth.authservice.api.users.User;

public record UpdateUserDto(Long id, String username, String email, String role) {

	public UpdateUserDto(Long id, String username, String email, String role) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.role = role.isBlank() ? "USER" : role;
	}

	public User updateUser() {
		return new User(this.id, this.username, this.email, this.role);
	}
}
