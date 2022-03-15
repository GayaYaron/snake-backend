package com.projects.snake.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.projects.snake.service.detail.UserDetail;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class TokenFilter implements Filter, ApplicationContextAware{
	private ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		 HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String method = httpRequest.getMethod();
		if (method.equals("OPTIONS")) {
			chain.doFilter(httpRequest, httpResponse);
		} else {
			String token = httpRequest.getHeader("Authorization");
			try {
				token = token.substring(7);
				Claims claims = Jwts.parserBuilder()
						.setSigningKey("sopegksldkjtdofjyslkethjdrihkgsrtgjnhsoerkyudrhklgsktsjgdfkguwnbosertlachmanntheking".getBytes())
						.build().parseClaimsJws(token).getBody();
						
				UserDetail userDetail = context.getBean(UserDetail.class);
				userDetail.setId(Integer.parseInt(claims.getSubject()));
				userDetail.setChosenDesignId(Integer.valueOf(claims.get("chosenDesign").toString()));

			} catch (Exception e) {
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
			chain.doFilter(httpRequest, httpResponse);
		}
		 
		
	}

}
