package dev.api.auth.authservice.api.auth.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {
	private String email;
	private String username;
	private String password;
	private String role;
	private String clientId;
}
