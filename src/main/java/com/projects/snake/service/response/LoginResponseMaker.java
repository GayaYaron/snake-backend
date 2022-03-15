package com.projects.snake.service.response;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projects.snake.exception.util.NullUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class LoginResponseMaker {
	@Autowired
	private NullUtil nullUtil;

	public LoginResponse make(Integer id, Integer chosenDesign ,String nickname, int coins) {
		nullUtil.check(id, "id");
		if (nickname == null) {
			nickname = "";
		}
		Date expitarion = new Date(System.currentTimeMillis() + 5400000);
		String jwts = Jwts.builder()
				.setIssuer("SnakeWebsite")
				.setSubject("" + id)
				.setIssuedAt(new Date())
				.setExpiration(expitarion)
				.signWith(Keys.hmacShaKeyFor(
						"sopegksldkjtdofjyslkethjdrihkgsrtgjnhsoerkyudrhklgsktsjgdfkguwnbosertlachmanntheking"
								.getBytes()))
				.claim("chosenDesign", chosenDesign)
				.compact();
		return new LoginResponse(id, chosenDesign, nickname, jwts, expitarion, coins);
	}
}
