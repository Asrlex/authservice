package dev.api.auth.authservice.common.kafka.consumers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.api.auth.authservice.api.users.dtos.UserDto;
import dev.api.auth.authservice.common.kafka.KafkaMessage;
import dev.api.auth.authservice.common.kafka.KafkaService;
import dev.api.auth.authservice.common.kafka.events.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for handling user-related events.
 */
@Slf4j(topic = "KafkaUserConsumer")
@Component
public class KafkaUserConsumer extends KafkaGenericConsumer<UserDto> {

	private static final String TOPIC = KafkaTopics.USER_EVENTS;

	public KafkaUserConsumer(ObjectMapper mapper, KafkaService kafkaService) {
		super(mapper, kafkaService);
	}

	/**
	 * Listens to the KafkaTopics.USER_EVENTS topic and processes incoming messages.
	 *
	 * @param messageJson the JSON message received from Kafka
	 */
	@KafkaListener(topics = TOPIC, groupId = "springmvc-group")
	public void consume(String messageJson) {
		super.consume(messageJson, new TypeReference<KafkaMessage<UserDto>>() {});
	}

//	/**
//	 * Handles the user event based on its type.
//	 *
//	 * @param message the Kafka message containing the user event
//	 */
//	@Override
//	protected void handleEvent(KafkaMessage<UserDto> message) {
//		log.info("Handle user {} logic: {}", message.getType(), message.getPayload());
//		switch (message.getType()) {
//			case CREATE -> notifyWebSocketClients("User created: " + message.getPayload().getUsername());
//			case UPDATE -> notifyWebSocketClients("User updated: " + message.getPayload().getUsername());
//			case DELETE -> notifyWebSocketClients("User deleted: " + message.getPayload().getUsername());
//			case RESTORE -> notifyWebSocketClients("User restored: " + message.getPayload().getUsername());
//			default -> log.warn("Unknown event type: {}", message.getType());
//		}
//	}
//
//	/**
//	 * Notifies connected WebSocket clients about the user event.
//	 *
//	 * @param notification the notification message to send
//	 */
//	private void notifyWebSocketClients(String notification) {
//		sessionManager.broadcast(notification);
//	}
}
