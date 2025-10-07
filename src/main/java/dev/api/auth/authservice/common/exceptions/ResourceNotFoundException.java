package dev.api.auth.authservice.common.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private String entityId;

	public ResourceNotFoundException(String message, String entityId) {
		super(message);
		this.entityId = entityId;
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public String getEntityId() {
		return entityId;
	}
}
