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
	public ResponseEntity<String> register(
			@RequestBody RegisterRequest request
	) {
		return ResponseEntity.ok(service.register(request));
	}

	@PostMapping("/authenticate")
	public JwtResponse authenticate(Authentication authentication) {
		System.out.println(authentication.getName());
		System.out.println("zzzzzzzzzzzzzzzzzzzzzz");
		System.out.println(authentication.getName());
		System.out.println(authentication.getName());
		System.out.println(authentication.getName());
		return new JwtResponse(service.authenticate(authentication));

	}

	@PostMapping("/refresh-token")
	public void refreshToken(
			Authentication authentication
	) throws IOException {
		service.refreshToken(authentication);

	}

	@GetMapping("/hello")
	public ResponseEntity<String> hello() {
		return ResponseEntity.ok("hello");
	}
}
record JwtResponse(String token){}
