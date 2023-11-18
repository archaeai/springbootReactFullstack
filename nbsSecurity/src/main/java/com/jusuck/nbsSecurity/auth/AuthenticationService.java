package com.jusuck.nbsSecurity.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jusuck.nbsSecurity.config.JwtService;
import com.jusuck.nbsSecurity.entity.token.Token;
import com.jusuck.nbsSecurity.entity.token.TokenRepository;
import com.jusuck.nbsSecurity.entity.token.TokenType;
import com.jusuck.nbsSecurity.entity.user.User;
import com.jusuck.nbsSecurity.entity.user.UserRepository;
import com.jusuck.nbsSecurity.exception.EmailAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final UserRepository repository;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	public AuthenticationResponse register(RegisterRequest request) {
		Optional<User> existingUser = repository.findByEmail(request.getEmail());
		if (existingUser.isPresent()) {
			throw new EmailAlreadyExistsException("이미 등록된 이메일입니다.");
		}
		var user = User.builder()
				.firstname(request.getFirstname())
				.lastname(request.getLastname())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole())
				.build();
		var savedUser =repository.save(user);
		var jwtToken = jwtService.generateToken(user);
		var refreshToken =jwtService.generateRefreshToken(user);
		saveUserToken(savedUser, jwtToken);
		return AuthenticationResponse.builder()
				.accessToken(jwtToken)
				.refreshToken(refreshToken)
				.build();
	}

	private void revokeAllUserTokens(User user){
		var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
		if(validUserTokens.isEmpty()) {
			return;
		}
		validUserTokens.forEach(t -> {
			t.setExpired(true);
			t.setRevoked(true);
		});
	}

	private void saveUserToken(User user, String jwtToken) {
		var token = Token.builder()
				.user(user)
				.token(jwtToken)
				.tokenType(TokenType.BEARER)
				.revoked(false)
				.expired(false)
				.build();
		tokenRepository.save(token);
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
//		authenticationManager.authenticate(
//				new UsernamePasswordAuthenticationToken(
//						request.getEmail(),
//						request.getPassword()
//				)
//		);
		var user = repository.findByEmail(request.getEmail())
				.orElseThrow();

		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		revokeAllUserTokens(user);
		saveUserToken(user,jwtToken);
		return AuthenticationResponse.builder()
				.accessToken(jwtToken)
				.refreshToken(refreshToken)
				.build();
	}

	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
			return;
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if (userEmail != null) {
			var user = this.repository.findByEmail(userEmail).orElseThrow();
			if (jwtService.isTokenValid(refreshToken,user)) {
				var accessToken = jwtService.generateToken(user);
				revokeAllUserTokens(user);
				saveUserToken(user, accessToken);
				var authResponse = AuthenticationResponse.builder()
						.accessToken(accessToken)
						.refreshToken(refreshToken)
						.build();
				new ObjectMapper().writeValue(response.getOutputStream(),authResponse);
			}
		}
	}
}

