package com.projects.snake.controller.model;

import lombok.AllArgsConstructor;

import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDetail {
	private int statusCode;
	private int snakeErrorcode;
	private String message;
}
