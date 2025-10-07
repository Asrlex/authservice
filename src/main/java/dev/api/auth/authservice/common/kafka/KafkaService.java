package dev.api.auth.authservice.common.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for emitting messages to Kafka topics.
 */
@Service
public class KafkaService {
	private final KafkaTemplate<String, String> kafkaTemplate;

	public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	/**
	 * Emit a message to a Kafka topic.
	 *
	 * @param topic   the Kafka topic to which the message will be sent
	 * @param message the message to be sent
	 */
	public void emit(String topic, String message) {
		kafkaTemplate.send(topic, message);
	}

	/**
	 * Send a message to the Dead Letter Queue (DLQ) for a specific topic.
	 *
	 * @param originalTopic the original Kafka topic
	 * @param message       the message to be sent to the DLQ
	 */
	public void sendToDLQ(String originalTopic, String message) {
		emit(originalTopic + ".DLQ", message);
	}
}

