package dev.api.auth.authservice.common.audit;

import java.util.Optional;

public class AuditContext {
	private static final ThreadLocal<String> currentActor = new ThreadLocal<>();

	public static void setActor(String actor) {
		currentActor.set(actor);
	}

	public static Optional<String> getActor() {
		return Optional.ofNullable(currentActor.get());
	}

	public static void clear() {
		currentActor.remove();
	}
}
