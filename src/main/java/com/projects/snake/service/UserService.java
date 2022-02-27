package com.projects.snake.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.snake.exception.util.NullUtil;
import com.projects.snake.model.User;
import com.projects.snake.repository.DesignBaseRepo;
import com.projects.snake.repository.DesignRepo;
import com.projects.snake.repository.UserRepo;
import com.projects.snake.service.response.LoginResponse;
import com.projects.snake.service.response.LoginResponseMaker;

@Service
@Transactional
public class UserService {
	@Autowired
	private DesignBaseRepo designBaseRepo;
	@Autowired
	private DesignRepo designRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private LoginResponseMaker responseMaker;
	@Autowired
	private NullUtil nullUtil;
	
	public Optional<LoginResponse> login(String nickname, String password) {
		nullUtil.check(nickname, "nickname");
		nullUtil.check(password, "password");
		Optional<User> optionalUser = userRepo.findByNicknameAndPassword(nickname, password);
		if(optionalUser.isEmpty()) {
			return Optional.empty();
		}
		User user = optionalUser.get();
		return Optional.of(responseMaker.make(user.getId(), user.getNickname()));
	}
}
