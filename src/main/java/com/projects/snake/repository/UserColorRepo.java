package com.projects.snake.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.snake.model.UserColor;

@Repository
public interface UserColorRepo extends JpaRepository<UserColor, Integer>{
	public boolean existsByUserIdAndColorPackId(Integer userId, Integer colorPackId);
	public List<UserColor> findByUserId(Integer userId);
}
