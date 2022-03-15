package com.projects.snake.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenFilterConfig {

	@Bean
	public FilterRegistrationBean<TokenFilter> createTokenFilter() {
		FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<TokenFilter>();
		registrationBean.setFilter(tokenFilter());
		registrationBean.addUrlPatterns("/user/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	public TokenFilter tokenFilter() {
		return new TokenFilter();
	}

}
