package com.projects.snake.model;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.sun.istack.NotNull;

import lombok.Data;

@Entity
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@NotNull
	@Column(nullable = false)
	private String nickname;
	@NotNull
	@Column(nullable = false)
	private String password;
	private int coins;
	private Integer chosenDesign;
	@OneToMany(mappedBy = "user")
	private List<Design> designs;
	
}
