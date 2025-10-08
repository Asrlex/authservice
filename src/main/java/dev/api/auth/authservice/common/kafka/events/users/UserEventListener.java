package dev.api.auth.authservice.common.kafka.events.users;

import dev.api.auth.authservice.common.kafka.EntityEventPublisher;
import dev.api.auth.authservice.common.kafka.KafkaMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserEventListener {

	private final EntityEventPublisher eventPublisher;
	private final String TOPIC = "user-events-log";

	public UserEventListener(EntityEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleUserCreated(UserCreatedEvent event) {
		eventPublisher.publishEvent(TOPIC, KafkaMessage.KafkaMessageType.CREATE_ENTITY, event.user());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleUserUpdated(UserUpdatedEvent event) {
		eventPublisher.publishEvent(TOPIC, KafkaMessage.KafkaMessageType.UPDATE_ENTITY, event.user());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleUserDeleted(UserDeletedEvent event) {
		eventPublisher.publishEvent(TOPIC, KafkaMessage.KafkaMessageType.DELETE_ENTITY, event.id());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleUserRestored(UserRestoredEvent event) {
		eventPublisher.publishEvent(TOPIC, KafkaMessage.KafkaMessageType.RESTORE_ENTITY, event.user());
	}
}
