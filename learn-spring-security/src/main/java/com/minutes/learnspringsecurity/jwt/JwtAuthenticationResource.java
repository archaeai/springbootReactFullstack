package com.minutes.learnspringsecurity.jwt;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class JwtAuthenticationResource {

	private JwtEncoder jwtEncoder;

	public JwtAuthenticationResource(JwtEncoder jwtEncoder){
		this.jwtEncoder = jwtEncoder;
	}

	@PostMapping("/authenticate")
	public JwtResponse authenticate(Authentication authentication) {

		return new JwtResponse(createToken(authentication));
	}

	private String createToken(Authentication authentication) {
		var claims = JwtClaimsSet.builder()
								.issuer("self")
								.issuedAt(Instant.now())
								.expiresAt(Instant.now().plusSeconds(60 * 30))
								.subject(authentication.getName())
								.claim("scope",createScope(authentication))
								.claim("crud","crud")
								.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	private String createScope(Authentication authentication) {
		return authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
	}
}

record JwtResponse(String token){}
