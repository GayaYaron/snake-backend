package com.projects.snake.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.projects.snake.controller.model.ErrorDetail;
import com.projects.snake.exception.AlreadyPurchasedException;
import com.projects.snake.exception.util.ErrorCode;

@ControllerAdvice
@RestController
public class SnakeExceptionHandler {

	@ExceptionHandler(AlreadyPurchasedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorDetail alreadyPurchased(AlreadyPurchasedException e) {
		return new ErrorDetail(HttpStatus.CONFLICT.value(), ErrorCode.ALREADY_PURCHASED.getCode(), e.getMessage());
	}
}
