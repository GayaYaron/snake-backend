package com.projects.snake.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.sun.istack.NotNull;

import lombok.Data;

@Entity
@Data
public class DesignBase {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@NotNull
	private String borderColor;
	@NotNull
	private String snakeColor;
	@NotNull
	private String foodColor;
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DesignBase other = (DesignBase) obj;
		if (borderColor == null) {
			if (other.borderColor != null)
				return false;
		} else if (!borderColor.equals(other.borderColor))
			return false;
		if (foodColor == null) {
			if (other.foodColor != null)
				return false;
		} else if (!foodColor.equals(other.foodColor))
			return false;
		if (snakeColor == null) {
			if (other.snakeColor != null)
				return false;
		} else if (!snakeColor.equals(other.snakeColor))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((borderColor == null) ? 0 : borderColor.hashCode());
		result = prime * result + ((foodColor == null) ? 0 : foodColor.hashCode());
		result = prime * result + ((snakeColor == null) ? 0 : snakeColor.hashCode());
		return result;
	}
}
