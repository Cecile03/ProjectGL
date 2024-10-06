package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * Classe Detail
 * Bean représentant un objet Detail.
 * @author Louis Peculier
 *
 */
@Setter
@Getter
@Entity
@NamedQuery(name="Detail.findAll", query="SELECT e FROM Detail e")
@Table(name="detail")
public class Detail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "description")
	private String description;
	@Column(name = "mark", nullable = false)
	private int mark;

	@ManyToOne
	@JoinColumn(name="category_id", referencedColumnName="id") // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
	private Category category;

}
