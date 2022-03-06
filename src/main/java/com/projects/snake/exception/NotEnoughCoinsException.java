package com.projects.snake.exception;

public class NotEnoughCoinsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotEnoughCoinsException() {
		super();
	}

	public NotEnoughCoinsException(String productName) {
		super("you do not have enough coins to buy this "+productName);
	}

}
