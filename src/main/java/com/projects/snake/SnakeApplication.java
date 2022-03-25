package com.projects.snake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.projects.snake.service.UserService;

@SpringBootApplication
public class SnakeApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SnakeApplication.class, args);
		UserService service = context.getBean(UserService.class);
		service.register("gtest", "test");
	}

}
