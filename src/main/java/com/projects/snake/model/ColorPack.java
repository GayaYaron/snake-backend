package com.projects.snake.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class ColorPack {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private List<String> colors;
}
