package com.jusuck.nbsSecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
@RequiredArgsConstructor
@Service
public class JwtService {

	private final JwtEncoder jwtEncoder;

	@Value("${application.security.jwt.expiration}")
	private long jwtExpiration;

	@Value("${application.security.jwt.refresh-token.expiration}")
	private long refreshExpiration;

	public String generateToken(Authentication authentication) {
		return buildToken(authentication,jwtExpiration);
	}

	public String generateRefreshToken(Authentication authentication) {
		return buildToken(authentication,refreshExpiration);
	}

	private String buildToken(Authentication authentication, long expiration) {
		var claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusMillis(expiration))
				.subject(authentication.getName())
				.claim("position",authentication.getAuthorities())
				.claim("valid",true)
				.build();
		// jwtEncoder에서 JWTresourse 를 통해 key sining 함
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	public boolean isTokenValid(Authentication authentication, UserDetails userDetails) {
		final String username = authentication.getName();
		return (username.equals(userDetails.getUsername())) && !(authentication.isAuthenticated());
	}

//// new Date() 기본값이 현재시간이고 before을 통해서 기한이 현재보다 이후이면 false를 반환한다.
//	private boolean isTokenExpired( String token) {
//		return true;
//	}
}


record JwtResponse(String token){}

