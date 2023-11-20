package com.jusuck.nbsSecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
	private final LogoutHandler  logoutHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeHttpRequests(auth -> {
			auth
					.requestMatchers("/api/v1/auth/**")
					.permitAll()
					.anyRequest()
					.authenticated();
				});
		http.sessionManagement(
				session ->
						session.sessionCreationPolicy(
								SessionCreationPolicy.STATELESS
						));
		http.httpBasic(withDefaults());
		http.headers().frameOptions().sameOrigin();
		http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
		http.logout(
				logout ->
						logout
								.logoutUrl("/api/v1/auth/logout")
								.addLogoutHandler(logoutHandler)
								.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
				);
		return http.build();
	}
}
