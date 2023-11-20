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

	public AuthenticationResponse refreshToken(Authentication authentication) throws IOException {
		final String username;
		username = authentication.getName();
		if (username != null) {
			var user = this.repository.findByUsername(username).orElseThrow();

			// i need some validation here

			var accessToken = jwtService.generateToken(authentication);
			var refreshToken = jwtService.generateToken(authentication);
			revokeAllUserTokens(user);
			saveUserToken(user, accessToken);
			var authResponse = AuthenticationResponse.builder()
					.accessToken(accessToken)
					.refreshToken(refreshToken)
					.build();
			return AuthenticationResponse.builder()
					.accessToken(accessToken)
					.refreshToken(refreshToken)
					.build();
		}else throw new RuntimeException("유저가 없습니다. 토큰을 확인하세요 이 메시지 필요없도록 필터체인에 validation 추가할 것 "){

		};
	}
}

