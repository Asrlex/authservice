package dev.api.auth.authservice.common.kafka.consumers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.api.auth.authservice.common.kafka.KafkaMessage;
import dev.api.auth.authservice.common.kafka.KafkaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "KafkaGenericConsumer")
public abstract class KafkaGenericConsumer<T> {

	protected final ObjectMapper mapper;
	private final KafkaService kafkaService;

	public KafkaGenericConsumer(
			ObjectMapper mapper,KafkaService kafkaService) {
		this.mapper = mapper;
		this.kafkaService = kafkaService;
	}

	/**
	 * Consumes a Kafka message in JSON format, deserializes it, and processes it.
	 *
	 * @param messageJson the JSON message received from Kafka
	 * @param typeRef     the type reference for deserialization
	 */
	public void consume(String messageJson, TypeReference<KafkaMessage<T>> typeRef) {
		try {
			KafkaMessage<T> message = mapper.readValue(messageJson, typeRef);
//			handleEvent(message);
		} catch (Exception e) {
			onError(messageJson, e);
		}
	}

//	/**
//	 * Handles the Kafka event based on its type.
//	 *
//	 * @param message the Kafka message containing the event
//	 */
//	protected abstract void handleEvent(KafkaMessage<T> message);

	/**
	 * Handles errors that occur during message processing.
	 *
	 * @param raw the raw JSON message that failed to process
	 * @param e   the exception that was thrown
	 */
	protected void onError(String raw, Exception e) {
		log.error("Failed to process Kafka message: {} with exception {}", raw, e.getMessage());
		kafkaService.sendToDLQ("dlq-topic", raw);
	}
}
