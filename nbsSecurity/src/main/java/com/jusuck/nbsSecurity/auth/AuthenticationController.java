package com.jusuck.nbsSecurity.auth;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 클래스 선언부에 @RestController 어노테이션을 사용하여, 이 클래스가 RESTful 컨트롤러임을 명시합니다.
// @RequestMapping 어노테이션은 이 컨트롤러의 모든 메소드가 '/api/v1/auth' 경로를 기본 URL로 사용함을 나타냅니다.
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor // Lombok 라이브러리의 어노테이션으로, final 또는 @NonNull 필드에 대한 생성자를 자동으로 생성합니다.
public class AuthenticationController {

	// AuthenticationService 타입의 객체에 대한 의존성을 주입합니다. final로 선언되어 있으므로 생성자 주입을 사용합니다.
	private final AuthenticationService service;

	// 사용자 등록을 위한 API 엔드포인트.
	// @PostMapping 어노테이션은 HTTP POST 요청을 처리합니다.
	// @PreAuthorize 어노테이션은 메소드 수준의 보안을 설정합니다. 이 메소드는 'SCOPE_ROLE_ADMIN'과 'SCOPE_access' 권한이 있는 사용자만 호출할 수 있습니다.
	@PostMapping("/register")
	@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') and hasAuthority('SCOPE_access')")
	public void register(@RequestBody RegisterRequest request) {
		// 서비스 레이어의 register 메소드를 호출하여 사용자를 등록합니다.
		service.register(request);
	}

	// 사용자 인증을 위한 API 엔드포인트.
	// 이 메소드는 Authentication 객체를 받아서 사용자 인증을 처리하고 응답을 반환합니다.
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(Authentication authentication) {
		// 서비스 레이어의 authenticate 메소드를 호출하여 인증을 수행하고 그 결과를 반환합니다.
		return ResponseEntity.ok(service.authenticate(authentication));
	}

	// 토큰 갱신을 위한 API 엔드포인트.
	// 'SCOPE_refresh' 권한이 있는 사용자만 이 메소드를 호출할 수 있습니다.
	@PostMapping("/refresh-token")
	@PreAuthorize("hasAuthority('SCOPE_refresh')")
	public ResponseEntity<AuthenticationResponse> refreshToken(Authentication authentication) {
		// 서비스 레이어의 refreshToken 메소드를 호출하여 토큰을 갱신하고 그 결과를 반환합니다.
		return ResponseEntity.ok(service.refreshToken(authentication));
	}
}

