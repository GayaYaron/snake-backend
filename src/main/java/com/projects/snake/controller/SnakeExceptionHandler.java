package com.projects.snake.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.projects.snake.controller.model.ErrorDetail;
import com.projects.snake.exception.AlreadyPurchasedException;
import com.projects.snake.exception.LoginFailedException;
import com.projects.snake.exception.NoColorException;
import com.projects.snake.exception.NoDefaultDesignException;
import com.projects.snake.exception.util.ErrorCode;

@ControllerAdvice
@RestController
public class SnakeExceptionHandler {

	@ExceptionHandler(AlreadyPurchasedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorDetail alreadyPurchased(AlreadyPurchasedException e) {
		return new ErrorDetail(HttpStatus.CONFLICT.value(), ErrorCode.ALREADY_PURCHASED.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(LoginFailedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorDetail loginFailed(LoginFailedException e) {
		return new ErrorDetail(HttpStatus.UNAUTHORIZED.value(), ErrorCode.LOGIN_FAILED.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(NoColorException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorDetail noColor(NoColorException e) {
		return new ErrorDetail(HttpStatus.NOT_FOUND.value(), ErrorCode.NO_COLOR.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(NoDefaultDesignException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorDetail noDefaultDesign(NoDefaultDesignException e) {
		return new ErrorDetail(HttpStatus.NOT_FOUND.value(), ErrorCode.NO_DEFAULT.getCode(), e.getMessage());
	}
	
}
