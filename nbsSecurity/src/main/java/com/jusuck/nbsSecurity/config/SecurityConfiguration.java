package com.jusuck.nbsSecurity.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

// @Configuration 어노테이션은 이 클래스가 스프링 설정 클래스임을 나타냅니다.
// @EnableWebSecurity 어노테이션은 Spring Security 설정을 활성화합니다.
// @EnableMethodSecurity 어노테이션은 메소드 수준의 보안을 활성화합니다.
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
	// 로그아웃 핸들러를 주입합니다.
	private final LogoutHandler logoutHandler;
	private final BasicAuthenticationFilter basicAuthenticationFilter;
	private static final String[] WHITE_LIST_URL = {
		"/api/v1/auth/authenticate",
		"/v2/api-docs",
		"/v3/api-docs",
		"/v3/api-docs/**",
		"/swagger-resources",
		"/swagger-resources/**",
		"/configuration/ui",
		"/configuration/security",
		"/swagger-ui/**",
		"/webjars/**",
		"/swagger-ui.html"};

	static final String[] BASIC_AUTH_LIST = {
		"/api/v1/auth/authenticate"
	};

	// HTTP 보안을 구성하는 빈을 정의합니다.
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF 보호 기능을 비활성화합니다.
		http.csrf().disable();

		http.authorizeHttpRequests(auth ->
			auth
				.requestMatchers(BASIC_AUTH_LIST)
				.permitAll()
				.requestMatchers(WHITE_LIST_URL)
				.permitAll()
				.anyRequest().authenticated()
		);
		http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
		http.addFilterBefore(basicAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		// 세션 정책을 STATELESS로 설정하여 세션을 사용하지 않도록 합니다.
		http.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// 프레임 옵션을 sameOrigin으로 설정하여 동일 출처 정책을 준수합니다.
		http.headers().frameOptions().sameOrigin();

		// OAuth2 리소스 서버를 JWT로 구성합니다.

		// 로그아웃 구성을 설정합니다.
		http.logout(logout -> {
			logout.logoutUrl("/api/v1/auth/logout")
				.addLogoutHandler(logoutHandler)
				.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
		});

		// 구성된 HttpSecurity 객체를 반환합니다.
		return http.build();
	}
}
