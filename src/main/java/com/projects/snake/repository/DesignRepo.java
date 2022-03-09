package com.projects.snake.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.snake.model.Design;

@Repository
public interface DesignRepo extends JpaRepository<Design, Integer>{
	public boolean existsByIdUserId(Integer id, Integer userId);
	public Optional<Design> findByIdAndUserId(Integer id, Integer userId);
	public Optional<Design> findFirstByNameAndUserId(String name, Integer userId);
	public List<Design> findByNameAndUserId(String name, Integer userId);
	public List<Design> findByUserId(Integer userId);
}
