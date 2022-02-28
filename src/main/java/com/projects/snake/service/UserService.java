package com.projects.snake.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.snake.exception.NotFoundException;
import com.projects.snake.exception.util.NullUtil;
import com.projects.snake.model.Design;
import com.projects.snake.model.User;
import com.projects.snake.model.UserDesign;
import com.projects.snake.repository.DesignBaseRepo;
import com.projects.snake.repository.DesignRepo;
import com.projects.snake.repository.UserDesignRepo;
import com.projects.snake.repository.UserRepo;
import com.projects.snake.service.detail.UserDetail;
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
	private UserDesignRepo userDesignRepo;
	@Autowired
	private LoginResponseMaker responseMaker;
	@Autowired
	private NullUtil nullUtil;
	@Autowired
	private UserDetail detail;
	
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
	
	/**
	 * sets the chosen design of the user
	 * if the chosen design is not already in the user designs- adds it
	 * @param designId
	 * @throws NotFoundException- if such design does not exist in the database
	 */
	@Transactional(readOnly = false)
	public void setChosenDesign(Integer designId) {
		if(!(designId==null)) {
			Optional<Design> optionDesign = designRepo.findById(designId);
			if(optionDesign.isEmpty()) {
				throw new NotFoundException("design");
			}
			User user = getUser().get();
			user.setChosenDesign(designId);
			if(!(userDesignRepo.existsByUserIdAndDesignId(detail.getId(), designId))) {
				Design design = optionDesign.get();
				userDesignRepo.save(new UserDesign(user, design));
			}
			userRepo.save(user);
		}
	}
	
	/**
	 * 
	 * @return Optional<User> the details of the user if such exist
	 */
	public Optional<User> getUser() {
		return userRepo.findById(detail.getId());
	}
}
