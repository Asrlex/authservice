package dev.api.auth.authservice.api.auth.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Utility class for generating and hashing tokens.
 * Uses SHA-256 for hashing and SecureRandom for token generation.
 * Optionally uses a pepper from configuration for added security.
 */
@Component
public class TokenUtils {
	private final String pepper; // from config, optional

	public TokenUtils(@Value("${auth.token.pepper:}") String pepper){
		this.pepper = pepper == null ? "" : pepper;
	}

	public String hashToken(String token) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(token.getBytes(StandardCharsets.UTF_8));
			if (!pepper.isEmpty()) md.update(pepper.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	public String generateRandomToken(int byteLen) {
		byte[] b = new byte[byteLen];
		SecureRandomHolder.INSTANCE.nextBytes(b);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
	}

	private static class SecureRandomHolder {
		static final SecureRandom INSTANCE = new SecureRandom();
	}
}

