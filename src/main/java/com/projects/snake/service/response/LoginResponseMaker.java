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

	public LoginResponse make(Integer id, String nickname) {
		nullUtil.check(id, "id");
		if (nickname == null) {
			nickname = "";
		}
		Date expitarion = new Date(System.currentTimeMillis() + 5400000);
		String jwts = Jwts.builder().setIssuer("CouponsWebsite").setSubject("" + id).claim("clientName", nickname)
				.setIssuedAt(new Date()).setExpiration(expitarion)
				.signWith(Keys.hmacShaKeyFor(
						"sopegksldkjtdofjyslkethjdrihkgsrtgjnhsoerkyudrhklgsktsjgdfkguwnbosertioseritwiorlachmanntheking"
								.getBytes()))
				.compact();
		return new LoginResponse(id, nickname, jwts, expitarion);
	}
}
