package dev.api.auth.authservice.common.kafka.events.users;

import dev.api.auth.authservice.api.users.entities.dtos.UserDto;

public record UserRestoredEvent(UserDto user) {
}
