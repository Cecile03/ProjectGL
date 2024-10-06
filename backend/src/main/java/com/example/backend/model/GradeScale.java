package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe GradeScale
 * Bean représentant un objet GradeScale.
 * @author Louis Peculier
 *
 */
@Setter
@Getter
@Entity
@NamedQuery(name="GradeScale.findAll", query="SELECT e FROM GradeScale e")
@NamedQuery(name="GradeScale.findById", query="SELECT e FROM GradeScale e where e.id = :id")
@Table(name="gradescale")
public class GradeScale {

	@Id
	@Column(name = "id", nullable = false)
	private int id;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "description")
	private String description;
}
