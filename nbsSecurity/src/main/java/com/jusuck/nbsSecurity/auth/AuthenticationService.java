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

import java.util.Optional;

// Service 어노테이션은 이 클래스가 서비스 계층의 컴포넌트임을 나타내며 스프링이 관리하는 빈으로 등록합니다.
@Service
// RequiredArgsConstructor 어노테이션은 final로 선언된 필드나, @NonNull 어노테이션이 붙은 필드에 대해 생성자를 자동으로 생성합니다.
@RequiredArgsConstructor
public class AuthenticationService {
	// 필요한 의존성들을 주입합니다. final로 선언된 필드에 대한 생성자 주입이 이루어집니다.
	private final UserRepository repository;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	// 사용자 등록 로직을 처리하는 메소드입니다.
	public void register(RegisterRequest request) {
		// 사용자 ID를 기반으로 기존 사용자가 있는지 확인합니다.
		Optional<User> existingUser = repository.findByUserId(request.getUserId());
		// 이미 존재하는 사용자 ID가 있다면 예외를 발생시킵니다.
		if (existingUser.isPresent()) {
			throw new EmailAlreadyExistsException("이미 존재하는 Id 입니다.");
		}
		// 새 사용자 객체를 생성합니다.
		var user = User.builder()
				.userId(request.getUserId())
				.username(request.getUsername())
				.email(request.getEmail())
				// 비밀번호를 암호화하여 저장합니다.
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole())
				.build();
		// 새 사용자 객체를 저장합니다.
		repository.save(user);
	}

	// 사용자의 모든 유효한 토큰을 취소하는 메소드입니다.
	private void revokeAllUserTokens(User user){
		var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getUserId());
		if(validUserTokens.isEmpty()) {
			return;
		}
		validUserTokens.forEach(t -> {
			t.setExpired(true);
			t.setRevoked(true);
		});
	}

	// 사용자의 새 토큰을 저장하는 메소드입니다.
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

	// 사용자 인증 및 토큰 발급 로직을 처리하는 메소드입니다.
	public AuthenticationResponse authenticate(Authentication authentication) {
		// JWT 토큰을 생성합니다.
		var jwtToken = jwtService.generateToken(authentication);
		// 리프레시 토큰을 생성합니다.
		var refreshToken = jwtService.generateRefreshToken(authentication);
		// 인증 응답 객체를 생성하고 반환합니다.
		return AuthenticationResponse.builder()
				.accessToken(jwtToken)
				.refreshToken(refreshToken)
				.build();
	}

	// 리프레시 토큰을 통한 인증 토큰 갱신 로직을 처리하는 메소드입니다.
	public AuthenticationResponse refreshToken(Authentication authentication) {
		final String username = authentication.getName();

		// username이 비어있는 경우 예외 처리
		if (username.isEmpty()) {
			throw new IllegalArgumentException("Username is required for refreshing token.");
		}

		// 사용자 조회
		var user = this.repository.findByUserId(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		// 새 액세스 토큰 및 리프레시 토큰을 생성합니다.
		var accessToken = jwtService.generateToken(authentication);
		var refreshToken = jwtService.generateToken(authentication);

		// 인증 응답 객체를 생성하고 반환합니다.
		return AuthenticationResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}
}
