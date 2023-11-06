package com.minutes.learnspringsecurity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HellowolrdResource {

	@GetMapping("hello-world")
	public String helloworld() {
		return "hello world user";
	}
}
