package dev.api.auth.authservice.common.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * Service for publishing entity events to Kafka topics.
 */
@Service
public class EntityEventPublisher {

	private final KafkaService kafkaService;
	private final ObjectMapper objectMapper;

	public EntityEventPublisher(KafkaService kafkaService, ObjectMapper objectMapper) {
		this.kafkaService = kafkaService;
		this.objectMapper = objectMapper;
	}

	/**
	 * Publishes an event to a specified Kafka topic.
	 *
	 * @param topic   the Kafka topic to which the event will be published
	 * @param type    the type of the event (CREATE, UPDATE, DELETE, RESTORE)
	 * @param payload the payload of the event
	 * @param <T>     the type of the payload
	 */
	public <T> void publishEvent(String topic, KafkaMessage.KafkaMessageType type, T payload) {
		KafkaMessage<T> message = new KafkaMessage<>(type, payload);
		try {
			String messageJson = objectMapper.writeValueAsString(message);
			kafkaService.emit(topic, messageJson);
		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize Kafka message", e);
		}
	}
}
