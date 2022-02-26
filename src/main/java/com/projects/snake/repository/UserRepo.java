package com.projects.snake.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.snake.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer>{
	public Optional<User> findByNicknameAndPassword(String nickname, String Password);
}
