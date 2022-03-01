package com.projects.snake.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.snake.model.UserColor;

@Repository
public interface UserColorRepo extends JpaRepository<UserColor, Integer>{
	public boolean existsByUserIdAndColorPackId(Integer userId, Integer colorPackId);
}
