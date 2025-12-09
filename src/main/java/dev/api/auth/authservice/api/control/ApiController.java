package dev.api.auth.authservice.api.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class ApiController {
	private final ApiService apiService;

	public ApiController(ApiService apiService) {
		this.apiService = apiService;
	}

	@GetMapping("/health")
	@Operation(summary = "Health Check", description = "Check the health status of the application")
	@RequestBody(
			description = "Health check payload",
			required = true
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Application is healthy"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "403", description = "Forbidden access")
	})
	public String healthCheck() {
		return this.apiService.healthCheck();
	}
}
