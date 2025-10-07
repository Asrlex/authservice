package dev.api.auth.authservice.common.exceptions;

public class RateLimitExceededException extends RuntimeException{
	public RateLimitExceededException(String message) {
		super(message);
	}
}
