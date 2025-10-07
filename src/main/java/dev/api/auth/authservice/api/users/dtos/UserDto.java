package dev.api.auth.authservice.api.users.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {

	private Long id;
	private String username;
	private String email;
	private String role;

	public UserDto(Long id, String username, String email, String role) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.role = role;
	}
}
