package com.projects.snake.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.snake.model.Design;
import com.projects.snake.model.DesignBase;

@Repository
public interface DesignRepo extends JpaRepository<Design, Integer>{
	public boolean existsByNameAndBase(String name, DesignBase base);
}
