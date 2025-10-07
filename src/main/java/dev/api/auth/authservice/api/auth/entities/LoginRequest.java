package dev.api.auth.authservice.api.auth.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
	private String email;
	private String password;
	private String clientId;
}
