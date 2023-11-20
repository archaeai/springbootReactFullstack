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

// Lombok의 @RequiredArgsConstructor를 사용하여 final 필드에 대한 생성자를 자동으로 생성합니다.
// @Service 어노테이션은 이 클래스가 서비스 계층의 컴포넌트임을 나타냅니다.
@RequiredArgsConstructor
@Service
public class JwtService {

	// JwtEncoder를 주입받습니다. 이는 JWT를 인코딩하는 데 사용됩니다.
	private final JwtEncoder jwtEncoder;

	// application.properties 파일로부터 JWT의 만료 시간을 주입받습니다.
	@Value("${application.security.jwt.expiration}")
	private long jwtExpiration;

	// application.properties 파일로부터 리프레쉬 토큰의 만료 시간을 주입받습니다.
	@Value("${application.security.jwt.refresh-token.expiration}")
	private long refreshExpiration;

	// 액세스 토큰을 생성하는 메소드입니다.
	public String generateToken(Authentication authentication) {
		String type = "access";
		return buildToken(authentication, jwtExpiration, type);
	}

	// 리프레쉬 토큰을 생성하는 메소드입니다.
	public String generateRefreshToken(Authentication authentication) {
		String type = "refresh";
		return buildToken(authentication, refreshExpiration, type);
	}

	// JWT를 구성하고 인코딩하는 메소드입니다.
	private String buildToken(Authentication authentication, long expiration, String type) {
		var claims = JwtClaimsSet.builder()
			.issuer("self") // 토큰 발행자
			.issuedAt(Instant.now()) // 발행 시간
			.expiresAt(Instant.now().plusMillis(expiration)) // 만료 시간
			.subject(authentication.getName()) // 토큰 주체
			.claim("scope", createScope(authentication, type)) // 'scope' 클레임에 권한과 토큰 타입 추가
			.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	// 토큰의 'scope' 클레임을 생성하는 메소드입니다.
	private List<String> createScope(Authentication authentication, String type) {
		List<String> roles = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.toList());
		roles.add(type); // 토큰 타입을 'scope' 클레임에 추가
		return roles;
	}

	// 토큰의 유효성을 검증하는 메소드입니다.
	public boolean isTokenValid(Authentication authentication, UserDetails userDetails) {
		final String username = authentication.getName();
		return (username.equals(userDetails.getUsername())) && !(authentication.isAuthenticated());
	}
}

