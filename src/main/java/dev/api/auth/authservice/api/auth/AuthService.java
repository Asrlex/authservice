package dev.api.auth.authservice.api.auth;

import dev.api.auth.authservice.api.auth.entities.IssuedTokens;
import dev.api.auth.authservice.api.auth.entities.LoginRequest;
import dev.api.auth.authservice.api.auth.entities.PasswordResetToken;
import dev.api.auth.authservice.api.auth.entities.RegisterRequest;
import dev.api.auth.authservice.api.auth.utils.TokenUtils;
import dev.api.auth.authservice.api.users.User;
import dev.api.auth.authservice.api.users.UserRepository;
import dev.api.auth.authservice.api.users.dtos.PasswordChange;
import dev.api.auth.authservice.common.exceptions.ResourceAlreadyInUseException;
import dev.api.auth.authservice.common.exceptions.ResourceNotFoundException;
import dev.api.auth.authservice.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	private final ResetTokenRepository resetTokenRepository;
	private final TokenUtils tokenUtils;

	public AuthService(JwtService jwtService,
					   UserRepository userRepository,
					   PasswordEncoder passwordEncoder,
					   RefreshTokenService refreshTokenService,
					   ResetTokenRepository resetTokenRepository,
					   TokenUtils tokenUtils) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.refreshTokenService = refreshTokenService;
		this.resetTokenRepository = resetTokenRepository;
		this.tokenUtils = tokenUtils;
	}

	/**
	 * Authenticate user and return JWT token
	 *
	 * @param dto      - login data
	 * @param req      - HTTP request
	 * @param response - HTTP response
	 * @return JWT token
	 */
	public Map<String, String> login(LoginRequest dto, HttpServletRequest req, HttpServletResponse response) {
		User user = userRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + dto.getEmail()));

		if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
			throw new BadCredentialsException("Invalid credentials");
		}

		Claims claims = jwtService.getAllClaims(req.getHeader(HttpHeaders.AUTHORIZATION));
		IssuedTokens tokens = refreshTokenService.createTokensForUser(
				user.getEmail(), claims, dto.getClientId(), req.getRemoteAddr(), req.getHeader(HttpHeaders.USER_AGENT)
		);
		response.addHeader(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(tokens.refreshToken(), refreshTokenService.getRefreshTtl().getSeconds()));

		return jwtService.mapUserClaimsToToken(user);
	}

	/**
	 * Register a new user and return JWT token
	 *
	 * @param dto 	- registration data
	 * @param response - HTTP response
	 * @return JWT token
	 */
	@Caching(evict = {
			@CacheEvict(value = "users", key = "'all'"),
			@CacheEvict(value = "users", key = "'allIncludingDeleted'")
	})
	public Map<String, String> register(RegisterRequest dto, HttpServletResponse response) {
		if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new ResourceAlreadyInUseException("Email already in use", dto.getEmail());
		}
		User newUser = new User(
				dto.getUsername(),
				dto.getEmail(),
				passwordEncoder.encode(dto.getPassword()),
				dto.getRole());
		User savedUser = userRepository.save(newUser);

		Map<String, String> token = jwtService.mapUserClaimsToToken(savedUser);
		Claims claims = jwtService.getAllClaims(token.get("access_token"));
		IssuedTokens tokens = refreshTokenService.createTokensForUser(
				savedUser.getEmail(), claims, dto.getClientId(), null, null
		);
		response.addHeader(HttpHeaders.SET_COOKIE,
				createRefreshTokenCookie(
						tokens.refreshToken(),
						refreshTokenService.getRefreshTtl().getSeconds()
				)
		);
		return token;
	}

	/**
	 * Logout user and invalidate their refresh token
	 *
	 * @param body - request body containing optional "jti" to revoke specific token
	 * @param request - HTTP request
	 * @param response - HTTP response
	 */
	public void logout(Map<String,String> body, HttpServletRequest request, HttpServletResponse response) {
		String jtiStr = body.get("jti");
		if (jtiStr != null) refreshTokenService.revokeByJti(UUID.fromString(jtiStr));
		else if (request.getUserPrincipal() != null) {
			String userId = request.getUserPrincipal().getName();
			refreshTokenService.revokeAllForUser(userId);
		}
		response.addHeader(HttpHeaders.SET_COOKIE, createRefreshTokenCookie("", 0) );
	}

	/**
	 * Initiate password reset process for a user by email
	 *
	 * @param email - the user's email
	 */
	@Transactional
	public void initiatePasswordReset(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
		String token = tokenUtils.generateRandomToken(32);
		String tokenHash = tokenUtils.hashToken(token);
		PasswordResetToken passwordResetToken = new PasswordResetToken(
				user.getId(),
				tokenHash
		);
		resetTokenRepository.save(passwordResetToken);

		String resetLink = "https://yourapp.com/reset-password?token=" + token + "&email=" + email;
		// Simulate sending email by printing to console
		System.out.println("Password reset link for " + email + ": " + resetLink);
	}

	/**
	 * Complete password reset process for a user
	 *
	 * @param dto - map containing "email", "newPassword", and "resetToken"
	 */
	@Transactional
	public void completePasswordReset(PasswordChange dto) {
		PasswordResetToken resetToken = resetTokenRepository.findByTokenHash(tokenUtils.hashToken(dto.resetToken()))
				.orElseThrow(() -> new ResourceNotFoundException("Invalid or expired reset token"));
		if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(java.time.Instant.now())) {
			throw new ResourceNotFoundException("Invalid or expired reset token");
		}
		User user = userRepository.findById(resetToken.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found for reset token"));
		user.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
		userRepository.save(user);
		resetToken.setUsed(true);
		resetTokenRepository.save(resetToken);
		// Simulate sending confirmation email by printing to console
		System.out.println("Password reset completed for user: " + dto.resetToken());
	}

	/**
	 * Create a secure HttpOnly cookie for the refresh token
	 * @param value - the refresh token value
	 * @param maxAge - max age in seconds
	 * @return the Set-Cookie header string
	 */
	private String createRefreshTokenCookie(String value, long maxAge) {
		return ResponseCookie.from("refresh_token", value)
				.httpOnly(true)
				.secure(true)
				.path("/")
				.sameSite("Strict")
				.maxAge(Duration.ofSeconds(maxAge))
				.build()
				.toString();
	}
}
