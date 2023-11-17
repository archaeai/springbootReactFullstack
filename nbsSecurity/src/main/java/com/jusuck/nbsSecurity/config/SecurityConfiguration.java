package com.jusuck.nbsSecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
	private final LogoutHandler  logoutHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf()
				.disable()
				.authorizeHttpRequests()
				.requestMatchers("/api/v1/auth/**")
				.permitAll()
//				.requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
//				.requestMatchers(HttpMethod.GET,"/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
//				.requestMatchers(HttpMethod.POST,"/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
//				.requestMatchers(HttpMethod.PUT,"/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
//				.requestMatchers(HttpMethod.DELETE,"/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
//				.requestMatchers("/api/v1/admin/**").hasAnyRole(ADMIN.name())
//				.requestMatchers(HttpMethod.GET,"/api/v1/admin/**").hasAnyAuthority(ADMIN_READ.name())
//				.requestMatchers(HttpMethod.POST,"/api/v1/admin/**").hasAnyAuthority(ADMIN_CREATE.name())
//				.requestMatchers(HttpMethod.PUT,"/api/v1/admin/**").hasAnyAuthority(ADMIN_UPDATE.name())
//				.requestMatchers(HttpMethod.DELETE,"/api/v1/admin/**").hasAnyAuthority(ADMIN_DELETE.name())
				.anyRequest()
				.authenticated()
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.headers(headers ->headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
				//.authenticationProvider(authenticationProvider)
				.logout()
				.logoutUrl("/api/v1/auth/logout")
				.addLogoutHandler(logoutHandler)
				.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());

		return httpSecurity.build();
	}
}
