package com.minutes.learnspringsecurity.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TodoResource {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public static final List<Todo> TODO_LIST = List.of(new Todo("hahaha", "Learn aws"),
			new Todo("zzzzz", "Learn english"));

	//메뉴별로 권한 확인(소메뉴포함)
	//@PreAuthorize("hasAuthority('[SCOPE_ROLE_USER]') ")
	@GetMapping("/todos")
	public List<Todo> retrieveAllTodos(Authentication authentication) {
		System.out.println(authentication.getName());
		System.out.println(authentication.getAuthorities());
		Object principal =authentication.getPrincipal();

		if (principal instanceof Jwt) {
			Jwt jwt = (Jwt) principal;
			// 모든 클레임을 가져옵니다.
			Map<String, Object> claims = jwt.getClaims();
			// 콘솔에 모든 클레임 출력
			System.out.println(claims);
			// 특정 클레임을 가져옵니다. 예: 'sub' 클레임 (주로 사용자 ID에 해당)
			String subject = jwt.getClaim("sub");
			System.out.println("Subject: " + subject);
		}

		return TODO_LIST;
	}

	@GetMapping("/users/{username}/todos")
	public Todo retrieveTodosForSpecificUser(@PathVariable String username) {
		return TODO_LIST.get(0);
	}

	@PostMapping("/users/{username}/todos")
	public Todo createTodoForSpecificUser(@PathVariable String username, @RequestBody Todo todo) {
		logger.info("Create {} for {}",todo,username);
		return TODO_LIST.get(0);
	}

}

record Todo( String username,String description) {

}
