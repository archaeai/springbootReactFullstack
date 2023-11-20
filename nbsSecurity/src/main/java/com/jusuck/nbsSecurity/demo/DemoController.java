package com.jusuck.nbsSecurity.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/demo-controller")
public class DemoController {

	@GetMapping
	public ResponseEntity<String> sayHello(Authentication authentication) {
		// 기본 정보 출력
		System.out.println("Name: " + authentication.getName());
		System.out.println("Details: " + authentication.getDetails());
		System.out.println("Authorities: " + authentication.getAuthorities());
		System.out.println("Credentials: " + authentication.getCredentials());
		System.out.println("Principal: " + authentication.getPrincipal());

		// Principal 객체가 복잡한 경우, 추가 정보를 출력
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			System.out.println("UserDetails: " + userDetails.getUsername() + ", " + userDetails.getAuthorities());
		}

		// 전체 객체 출력
		System.out.println("Authentication object: " + authentication);

		return ResponseEntity.ok("say hello please");
	}

}
