package com.jusuck.nbsSecurity.demo;

import com.jusuck.nbsSecurity.auth.AuthenticationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/demo-controller")
public class DemoController {

	@GetMapping
	public ResponseEntity<String> sayHello(Authentication authentication) {
		System.out.println(authentication.getName());
		System.out.println(authentication.getAuthorities());
		return ResponseEntity.ok("say hello please");
	}
}
