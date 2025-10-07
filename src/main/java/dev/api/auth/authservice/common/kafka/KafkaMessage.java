package dev.api.auth.authservice.common.kafka;

import lombok.Getter;

import java.time.Instant;

public class KafkaMessage<T> {

	public enum Type {
		CREATE, UPDATE, DELETE, RESTORE
	}

	@Getter
	private final Type type;
	@Getter
	private final Instant timestamp;
	@Getter
	private final T payload;

	public KafkaMessage(Type type, T data) {
		this.type = type;
		this.payload = data;
		this.timestamp = Instant.now();
	}
}
