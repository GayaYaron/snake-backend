package com.projects.snake.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "base")
	private List<Design> designs;
}
