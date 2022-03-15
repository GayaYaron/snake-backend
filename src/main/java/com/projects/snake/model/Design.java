package com.projects.snake.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Design {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String name;
	private String snakeColor;
	private String borderColor;
	private String foodColor;
	@ManyToOne
	private User user;
	
	public Design(String name, String snakeColor, String borderColor, String foodColor, User user) {
		super();
		this.name = name;
		this.snakeColor = snakeColor;
		this.borderColor = borderColor;
		this.foodColor = foodColor;
		this.user = user;
	}
}
