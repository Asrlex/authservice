package dev.api.auth.authservice.common.exceptions;

public class ResourceAlreadyInUseException extends RuntimeException {

	private String entityId;

	public ResourceAlreadyInUseException(String message, String entityId) {
		super(message);
		this.entityId = entityId;
	}

	public ResourceAlreadyInUseException(String message) {
		super(message);
	}

	public String getEntityId() {
		return entityId;
	}
}
