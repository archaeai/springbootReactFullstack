package com.jusuck.nbsSecurity.entity.user;

import com.jusuck.nbsSecurity.entity.token.Token;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder //객체의 불변성을 지켜준다. 체이닝메소드를 사용할 수 있다. 필터체인을 생각해보면 된다.
@Entity
@Table(name = "_user")
public class User extends org.springframework.security.core.userdetails.User {
	private String email;

	public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
}
