package com.projects.snake.exception;

public class AlreadyPurchasedException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyPurchasedException(String productName) {
		super("You already purchasesd this "+productName+". Purchase cancled.");
	}

}
