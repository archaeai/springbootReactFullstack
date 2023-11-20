package com.jusuck.nbsSecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JwtService {

	private final JwtEncoder jwtEncoder;

	@Value("${application.security.jwt.expiration}")
	private long jwtExpiration;

	@Value("${application.security.jwt.refresh-token.expiration}")
	private long refreshExpiration;
// 액세스토큰 생성
	public String generateToken(Authentication authentication) {
		String type = "access";
		return buildToken(authentication,jwtExpiration,type);
	}
// 리프레쉬토큰 생성
	public String generateRefreshToken(Authentication authentication) {
		String type = "refresh";
		return buildToken(authentication,refreshExpiration,type);
	}

	private String buildToken(Authentication authentication, long expiration, String type) {
		var claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusMillis(expiration))
				.subject(authentication.getName())
				.claim("scope", createScope(authentication,type)) // type을 추가해서 access, refresh구분
				.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	private List<String> createScope(Authentication authentication,String type) {
		List<String> roles = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		roles.add(type);
		return roles;
	}


	public boolean isTokenValid(Authentication authentication, UserDetails userDetails) {
		final String username = authentication.getName();
		return (username.equals(userDetails.getUsername())) && !(authentication.isAuthenticated());
	}
}

