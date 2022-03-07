package com.projects.snake.exception;

public class NoColorException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoColorException(String colorType) {
		super("You do not own the color for the "+colorType);
	}
	
	

}
