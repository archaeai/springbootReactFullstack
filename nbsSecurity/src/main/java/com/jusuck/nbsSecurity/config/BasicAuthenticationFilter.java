package com.jusuck.nbsSecurity.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static com.jusuck.nbsSecurity.config.SecurityConfiguration.BASIC_AUTH_LIST;
// authenticate 시에 basic auth 통해서 base64 암호화 이용하여 보완 적용

@Component
@RequiredArgsConstructor
public class BasicAuthenticationFilter extends OncePerRequestFilter {
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String path = request.getRequestURI();

		List<String> BasicAuthList = Arrays.asList(BASIC_AUTH_LIST);
		if (BasicAuthList.contains(path)) {
			String header = request.getHeader("Authorization");

			if (header != null && header.startsWith("Basic ")) {
				String base64Credentials = header.substring("Basic ".length()).trim();
				byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
				String credentials = new String(credDecoded, StandardCharsets.UTF_8);

				final String[] values = credentials.split(":", 2);

				if (values.length == 2) {
					String userId = values[0];
					String password = values[1];

					UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

					if (passwordEncoder.matches(password, userDetails.getPassword())) {
						UsernamePasswordAuthenticationToken authentication =
								new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						SecurityContextHolder.getContext().setAuthentication(authentication);
					} else {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						return;
					}
				}
			}
		}

		filterChain.doFilter(request, response);
	}
}

