package com.projects.snake.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.snake.model.DesignBase;

@Repository
public interface DesignBaseRepo extends JpaRepository<DesignBase, Integer> {
	public Optional<DesignBase> existsByBorderColorAndSnakeColorAndFoodColor(String borderColor, String snakeColor,
			String foodColor);
}
