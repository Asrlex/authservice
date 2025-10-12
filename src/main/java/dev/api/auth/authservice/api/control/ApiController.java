package dev.api.auth.authservice.api.control;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class ApiController {
	private final ApiService apiService;

	public ApiController(ApiService apiService) {
		this.apiService = apiService;
	}

	@GetMapping("/health")
	public String healthCheck() {
		return this.apiService.healthCheck();
	}
}
