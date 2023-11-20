package com.jusuck.nbsSecurity.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lombok 라이브러리의 @Data 어노테이션을 사용하여,
// 이 클래스의 모든 필드에 대한 getter와 setter,
// toString, equals, hashCode 메서드를 자동으로 생성합니다.
@Data
// @Builder 어노테이션을 사용하여, 불변 객체 패턴을 위한 빌더를 자동으로 생성합니다.
// 이를 통해 클라이언트 코드에서 객체의 상태를 변경하지 않고 새 객체를 생성할 수 있습니다.
@Builder
// 모든 필드 값을 파라미터로 받는 생성자를 자동으로 생성합니다.
@AllArgsConstructor
// 기본 생성자를 자동으로 생성합니다.
@NoArgsConstructor
public class AuthenticationResponse {

	// @JsonProperty 어노테이션을 사용하여 JSON 키 이름을 지정합니다.
	// 이 필드는 JSON 객체에서 "access_token" 키에 해당하는 값을 저장하거나 읽어올 때 사용됩니다.
	@JsonProperty("access_token")
	private String accessToken;

	// @JsonProperty 어노테이션을 사용하여 JSON 키 이름을 지정합니다.
	// 이 필드는 JSON 객체에서 "refresh_token" 키에 해당하는 값을 저장하거나 읽어올 때 사용됩니다.
	@JsonProperty("refresh_token")
	private String refreshToken;

	// Lombok 라이브러리가 getter와 setter를 자동으로 생성하기 때문에
	// 명시적으로 추가할 필요가 없습니다.
}
