package com.projects.snake.exception;

public class LoginFailedException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginFailedException() {
		super("Nickname or password incorrect");
	}

}
