package com.jusuck.nbsSecurity.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lombok 라이브러리의 @Data 어노테이션을 사용하여,
// 이 클래스의 모든 필드에 대한 getter와 setter,
// toString, equals, hashCode 메서드를 자동으로 생성합니다.
@Data
// @Builder 어노테이션은 객체 생성시 불변성을 지키면서도 유연한 객체 생성을 위한
// 빌더 패턴을 구현할 수 있도록 도와주며, 체이닝 방식의 메소드 호출을 가능하게 합니다.
@Builder
// 모든 필드 값을 파라미터로 받는 생성자를 자동으로 생성합니다.
// 이는 클래스의 인스턴스를 생성할 때 편리하게 사용할 수 있습니다.
@AllArgsConstructor
// 파라미터가 없는 기본 생성자를 자동으로 생성합니다.
// JPA, Jackson 같은 라이브러리에서 객체를 생성할 때 필요합니다.
@NoArgsConstructor
public class RegisterRequest {

	// 사용자의 고유 식별자를 위한 필드입니다.
	private String userId;
	// 사용자의 이름을 저장하는 필드입니다.
	private String username;
	// 사용자의 이메일 주소를 저장하는 필드입니다.
	private String email;
	// 사용자의 비밀번호를 저장하는 필드입니다.
	// 이 필드의 값은 저장하기 전에 암호화 과정을 거쳐야 합니다.
	private String password;
	// 사용자의 역할(권한)을 나타내는 필드입니다.
	// 예: "ROLE_USER", "ROLE_ADMIN" 등
	private String role;
}

