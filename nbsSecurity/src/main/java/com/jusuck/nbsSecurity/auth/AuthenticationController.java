package com.jusuck.nbsSecurity.auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;
	@PostMapping("/register")
	public void register(
			@RequestBody RegisterRequest request
	) {}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(Authentication authentication) {
		return ResponseEntity.ok(service.authenticate(authentication));
	}

	@PostMapping("/refresh-token")
	public void refreshToken(
			Authentication authentication
	) throws IOException {
		service.refreshToken(authentication);

	}

}
