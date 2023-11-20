package com.jusuck.nbsSecurity.auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;
	@PostMapping("/register")
	@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') and hasAuthority('SCOPE_access')")
	public void register(
			@RequestBody RegisterRequest request
	) {
		service.register(request);
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(Authentication authentication) {
		return ResponseEntity.ok(service.authenticate(authentication));
	}

	@PostMapping("/refresh-token")
	@PreAuthorize("hasAuthority('SCOPE_refresh')") // Authority 가 refresh 인 경우에만
	public ResponseEntity<AuthenticationResponse> refreshToken(Authentication authentication) {
		return ResponseEntity.ok(service.refreshToken(authentication));
	}

}
