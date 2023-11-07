package com.minutes.learnspringsecurity.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TodoResource {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public static final List<Todo> TODO_LIST = List.of(new Todo("hahaha", "Learn aws"),
			new Todo("zzzzz", "Learn english"));

	@PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
	@GetMapping("/todos")
	public List<Todo> retrieveAllTodos(Authentication authentication) {
		System.out.println(authentication.getAuthorities());
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
