package com.projects.snake.model;


import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class ColorPack {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private int price;
	private ColorType type;
	@OneToMany(mappedBy = "pack", fetch = FetchType.EAGER)
	private List<ColorToPack> colors;
}
