package com.jusuck.nbsSecurity.entity.token;

import com.jusuck.nbsSecurity.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getter setter 자동생성 toString, equals, hasCode AllArgsConstructor 기능을 하는 구나
@Builder //객체의 불변성을 지켜준다. 체이닝메소드를 사용할 수 있다. 필터체인을 생각해보면 된다.
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token")
public class Token {
	@Id
	@GeneratedValue
	private Integer id;

	private String token;

	@Enumerated(EnumType.STRING)
	private TokenType tokenType;

	private boolean expired;

	private boolean revoked;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

}
