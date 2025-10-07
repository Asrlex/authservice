package dev.api.auth.authservice.security;

import dev.api.auth.authservice.api.users.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Service for generating and validating JWT tokens.
 */
@Service
public class JwtService {

	private final long EXPIRATION;
	private final Key key;

	public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.EXPIRATION = expiration;
	}

	/**
	 * Generates a JWT token with the given subject and claims.
	 *
	 * @param subject the subject (typically the username)
	 * @param claims  additional claims to include in the token
	 * @return the generated JWT token
	 */
	public String generateAccessToken(String subject, Map<String, Object> claims) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Parses and validates the given JWT token.
	 *
	 * @param token the JWT token to parse
	 * @return the parsed JWS containing the claims
	 * @throws JwtException if the token is invalid or expired
	 */
	public Jws<Claims> parseToken(String token) throws JwtException {
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
	}

	/**
	 * Map user claims to JWT token
	 * @param user - user entity
	 * @return map containing JWT token
	 */
	public Map<String, String> mapUserClaimsToToken(User user) {
		Map<String, Object> claims = Map.of(
				"id", user.getId(),
				"username", user.getUsername(),
				"role", user.getRole()
		);

		String token = generateAccessToken(user.getEmail(), claims);
		return Map.of("access_token", token);
	}

	/**
	 * Validates the given JWT token.
	 *
	 * @param token the JWT token to validate
	 * @throws JwtException if the token is invalid or expired
	 */
	public void validateToken(String token) throws JwtException {
		parseToken(token);
	}

	/**
	 * Extracts the subject from the given JWT token.
	 *
	 * @param token the JWT token
	 * @return the subject (typically the username)
	 * @throws JwtException if the token is invalid or expired
	 */
	public String getSubject(String token) {
		return parseToken(token).getBody().getSubject();
	}

	/**
	 * Extracts all claims from the given JWT token.
	 *
	 * @param token the JWT token
	 * @return the claims contained in the token
	 * @throws JwtException if the token is invalid or expired
	 */
	public Claims getAllClaims(String token) {
		return parseToken(token).getBody();
	}
}
