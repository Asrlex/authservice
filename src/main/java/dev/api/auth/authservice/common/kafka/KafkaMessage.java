package dev.api.auth.authservice.common.kafka;

import lombok.Getter;

import java.time.Instant;

public class KafkaMessage<T> {

	public enum KafkaMessageType {
		CREATE_ENTITY, UPDATE_ENTITY, DELETE_ENTITY, RESTORE_ENTITY, EMAIL
	}

	@Getter
	private final KafkaMessageType type;
	@Getter
	private final Instant timestamp;
	@Getter
	private final T payload;

	public KafkaMessage(KafkaMessageType type, T data) {
		this.type = type;
		this.payload = data;
		this.timestamp = Instant.now();
	}
}
