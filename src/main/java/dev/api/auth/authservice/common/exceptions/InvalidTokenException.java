package dev.api.auth.authservice.common.exceptions;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {

	private String entityId;

	public InvalidTokenException(String message, String entityId) {
		super(message);
		this.entityId = entityId;
	}

	public InvalidTokenException(String message) {
		super(message);
	}
}
