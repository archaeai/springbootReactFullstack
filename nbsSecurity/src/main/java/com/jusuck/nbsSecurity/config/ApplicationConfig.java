package com.jusuck.nbsSecurity.config;

import com.jusuck.nbsSecurity.entity.user.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import java.util.UUID;

// @RequiredArgsConstructor 어노테이션은 final 필드에 대한 생성자를 자동으로 생성합니다.
// @Configuration 어노테이션은 이 클래스가 스프링 설정 클래스임을 나타냅니다.
// @EnableJpaAuditing 어노테이션은 JPA Auditing 기능을 활성화합니다.
@RequiredArgsConstructor
@Configuration
@EnableJpaAuditing
public class ApplicationConfig {

	// UserRepository를 주입받습니다. 이는 사용자 정보에 접근하기 위해 사용됩니다.
	private final UserRepository repository;

	// UserDetailsService를 정의하는 빈을 생성합니다.
	// 이 서비스는 사용자의 세부 정보를 로드하는 데 사용됩니다.
	@Bean
	public UserDetailsService userDetailsService() {
		return userId -> repository.findByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	// AuthenticationManager를 정의하는 빈을 생성합니다.
	// 이는 사용자 인증에 사용됩니다.
	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
		var authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		return new ProviderManager(authenticationProvider);
	}

	// BCryptPasswordEncoder를 정의하는 빈을 생성합니다.
	// 이는 비밀번호 암호화에 사용됩니다.
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// RSA 키 쌍을 생성하는 빈을 정의합니다.
	@Bean
	public KeyPair keyPair()  {
		try {
			var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			return keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// RSA 키를 사용하여 RSAKey 객체를 생성하는 빈을 정의합니다.
	@Bean
	public RSAKey rsaKey(KeyPair keyPair) {
		return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
				.privateKey(keyPair.getPrivate())
				.keyID(UUID.randomUUID().toString())
				.build();
	}

	// JWKSource 객체를 생성하는 빈을 정의합니다.
	// 이는 JWT 토큰을 처리하는데 사용됩니다.
	@Bean
	public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
		var jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, context) -> jwkSelector.select(jwkSet);
	}

	// JwtDecoder를 정의하는 빈을 생성합니다.
	// 이는 인커밍 JWT를 디코딩하는 데 사용됩니다.
	@Bean
	public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
		return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
	}

	// JwtEncoder를 정의하는 빈을 생성합니다.
	// 이는 JWT를 인코딩하는 데 사용됩니다.
	@Bean
	public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
		return new NimbusJwtEncoder(jwkSource);
	}

	// 현재 인증된 사용자의 이름을 제공하는 AuditorAware 구현을 생성하는 빈을 정의합니다.
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
				.map(Authentication::getName);
	}
}
