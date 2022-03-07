package com.projects.snake.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.sun.istack.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class DesignBase {
	private Integer id;
	@NotNull
	@Column(nullable = false)
	private String snakeColor;
	@NotNull
	@Column(nullable = false)
	private String borderColor;
	@NotNull
	@Column(nullable = false)
	private String foodColor;
}
