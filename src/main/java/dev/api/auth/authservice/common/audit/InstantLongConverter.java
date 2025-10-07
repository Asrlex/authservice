package dev.api.auth.authservice.common.audit;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;

@Converter(autoApply = true)
public class InstantLongConverter implements AttributeConverter<Instant, Long> {
	@Override
	public Long convertToDatabaseColumn(Instant instant) {
		return instant == null ? null : instant.toEpochMilli();
	}

	@Override
	public Instant convertToEntityAttribute(Long epochMilli) {
		return epochMilli == null ? null : Instant.ofEpochMilli(epochMilli);
	}
}
