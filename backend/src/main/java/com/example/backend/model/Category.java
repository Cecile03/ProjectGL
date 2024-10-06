package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe Category
 * Bean représentant un objet Category.
 * @author Louis Peculier
 *
 */
@Setter
@Getter
@Entity
@NamedQuery(name="Category.findAll", query="SELECT e FROM Category e")
@NamedQuery(name="Category.findById", query="SELECT e FROM Category e where e.id = :id")
@Table(name="category")
public class Category {

	@Id
	@Column(name = "id", nullable = false)
	private int id;
	@Column(name = "name", nullable = false)
	private String name;
	@ManyToOne
	@JoinColumn(name="grade_scale_id", referencedColumnName="id") // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
	private GradeScale gradeScale;

}
