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
public class ColorToPack {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@ManyToOne
	private ColorPack pack;
	@ManyToOne
	private StringEnt color;
	
	public ColorToPack(ColorPack pack, StringEnt color) {
		super();
		this.pack = pack;
		this.color = color;
	}
}
