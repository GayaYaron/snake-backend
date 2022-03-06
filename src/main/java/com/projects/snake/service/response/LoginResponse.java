package com.projects.snake.service.response;

import java.util.Date;

import com.projects.snake.model.Design;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
	private Integer id;
	private String nickname;
	private String jwtToken;
	private Date expiration;
	private Design chosenDesign;
}
