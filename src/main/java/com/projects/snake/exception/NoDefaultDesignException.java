package com.projects.snake.exception;

public class NoDefaultDesignException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoDefaultDesignException() {
		super("You must always have a design named 'defualt'.");
	}

}
