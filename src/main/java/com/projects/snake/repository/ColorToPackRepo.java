package com.projects.snake.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.snake.model.ColorToPack;

@Repository
public interface ColorToPackRepo extends JpaRepository<ColorToPack, Integer>{

}
