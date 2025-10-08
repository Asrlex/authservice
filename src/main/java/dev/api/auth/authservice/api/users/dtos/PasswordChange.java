package dev.api.auth.authservice.api.users.dtos;

public record PasswordChange(
		Long userId,
		String resetToken,
		String currentPassword,
		String newPassword
) {}
