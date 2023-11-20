package com.jusuck.nbsSecurity.auth;

import com.jusuck.nbsSecurity.config.JwtService;
import com.jusuck.nbsSecurity.entity.token.Token;
import com.jusuck.nbsSecurity.entity.token.TokenRepository;
import com.jusuck.nbsSecurity.entity.token.TokenType;
import com.jusuck.nbsSecurity.entity.user.User;
import com.jusuck.nbsSecurity.entity.user.UserRepository;
import com.jusuck.nbsSecurity.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

	public void register(RegisterRequest request) {
		Optional<User> existingUser = repository.findByUsername(request.getUsername());
		if (existingUser.isPresent()) {
			throw new EmailAlreadyExistsException("이미 등록된 이메일입니다.");
		}
		var user = User.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole())
				.build();
		repository.save(user);
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

	public AuthenticationResponse authenticate(Authentication authentication) {

		var jwtToken = jwtService.generateToken(authentication);
		var refreshToken = jwtService.generateRefreshToken(authentication);
		return AuthenticationResponse.builder()
				.accessToken(jwtToken)
				.refreshToken(refreshToken)
				.build();
	}

	public AuthenticationResponse refreshToken(Authentication authentication) {
		final String username = authentication.getName();

		// username이 비어있는 경우 예외 처리
		if (username.isEmpty()) {
			throw new IllegalArgumentException("Username is required for refreshing token.");
		}

		// 사용자 조회
		var user = this.repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		// 토큰 생성
		var accessToken = jwtService.generateToken(authentication);
		var refreshToken = jwtService.generateToken(authentication);

		// 응답 반환
		return AuthenticationResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

}

