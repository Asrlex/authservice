package dev.api.auth.authservice.api.auth.entities;

import java.util.UUID;

public record IssuedTokens(String accessToken, String refreshToken, UUID refreshJti) {}

