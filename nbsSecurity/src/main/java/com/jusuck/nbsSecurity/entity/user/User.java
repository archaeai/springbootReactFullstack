package com.jusuck.nbsSecurity.entity.user;

import com.jusuck.nbsSecurity.entity.token.Token;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User extends org.springframework.security.core.userdetails.User {
	private String email;

	private String role; // 문자열로 변경

	@OneToMany(mappedBy = "user")
	private List<Token> tokens;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 문자열 기반의 role을 처리하는 로직
		return Collections.singletonList(new SimpleGrantedAuthority(this.role));
	}
}
