package com.projects.snake.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.snake.model.StringEnt;

@Repository
public interface StringEntRepo extends JpaRepository<StringEnt, String>{

}
