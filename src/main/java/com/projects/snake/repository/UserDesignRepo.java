package com.projects.snake.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.snake.model.UserDesign;

@Repository
public interface UserDesignRepo extends JpaRepository<UserDesign, Integer>{
	public boolean existsByUserIdAndDesignId(Integer userId, Integer designId);
	
	public List<UserDesign> findByUserId(Integer userId);
	
}