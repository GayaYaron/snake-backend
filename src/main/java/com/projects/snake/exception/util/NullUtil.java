package com.projects.snake.exception.util;

import org.springframework.stereotype.Component;

import com.projects.snake.exception.NullVariableException;

@Component
public class NullUtil {
	public void check(Object o) throws NullVariableException{
		if(o==null) {
			throw new NullVariableException();
		}
	}
	
	public void check(Object o, String name) throws NullVariableException{
		if(o==null) {
			throw new NullVariableException(name);
		}
	}
	
}
