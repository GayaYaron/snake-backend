package com.projects.snake.exception.util;

public enum ErrorCode {
	ALREADY_PURCHASED(1), LOGIN_FAILED(2), NO_COLOR(3), NO_DEFAULT(4), NOT_ENOUGH_COINS(5), NOT_FOUND(6),
	NULL_VARIABLE(7);

	private int code;

	private ErrorCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
