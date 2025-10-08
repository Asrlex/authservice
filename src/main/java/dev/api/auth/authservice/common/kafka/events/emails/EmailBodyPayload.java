package dev.api.auth.authservice.common.kafka.events.emails;

public record EmailBodyPayload (
	String to,
	String subject,
	String body
) {}
