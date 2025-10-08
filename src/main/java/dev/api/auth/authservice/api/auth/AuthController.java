package dev.api.auth.authservice.api.auth;

import dev.api.auth.authservice.api.auth.entities.LoginRequest;
import dev.api.auth.authservice.api.auth.entities.RegisterRequest;
import dev.api.auth.authservice.api.users.dtos.PasswordChange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	@Operation(summary = "User Login", description = "Authenticate a user and return a JWT token")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "Login payload",
		required = true
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successful login"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden access")
	})
	public Map<String, String> login(@RequestBody LoginRequest dto, HttpServletRequest request, HttpServletResponse response) {
		return this.authService.login(dto, request, response);
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "User Registration", description = "Register a new user")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "Registration payload",
		required = true
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Successful registration"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden access")
	})
	public Map<String, String> register(@RequestBody RegisterRequest dto, HttpServletResponse response) {
		return this.authService.register(dto, response);
	}

	@PostMapping("/start-password-reset")
	@Operation(summary = "Initiate Password Reset", description = "Initiate password reset process for a user")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Password reset payload",
			required = true
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset initiated successfully"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden access"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
	})
	public void initiatePasswordReset(@RequestBody String email) {
		this.authService.initiatePasswordReset(email);
	}

	@PostMapping("/password-reset")
	@Operation(summary = "Complete Password Reset", description = "Complete password reset process for a user")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Password reset payload",
			required = true
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset successfully"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden access"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
	})
	public void completePasswordReset(@RequestBody PasswordChange dto) {
		this.authService.completePasswordReset(dto);
	}

	@PostMapping("/logout")
	@Operation(summary = "User Logout", description = "Logout a user and invalidate their refresh token")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successful logout"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid token"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden access")
	})
	public void logout(@RequestBody Map<String,String> body, HttpServletRequest request, HttpServletResponse response) {
		this.authService.logout(body, request, response);
	}
}

