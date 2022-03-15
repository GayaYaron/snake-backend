package com.projects.snake.service.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
	private Integer id;
	private Integer chosenDesign;
	private String nickname;
	private String jwtToken;
	private Date expiration;
	private int coins;
}
