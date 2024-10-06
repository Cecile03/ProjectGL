package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;

/**
 * Classe Sprint
 * Bean représentant un objet Sprint.
 * @author Louis Peculier
 *
 */
@Entity
@Setter
@Getter
@NamedQuery(name="Sprint.findAll", query="SELECT e FROM Sprint e")
@NamedQuery(name="Sprint.findById", query="SELECT e FROM Sprint e WHERE e.id = :id")
@Table(name="Sprint")
public class Sprint implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	@Column(name = "start_date", nullable = false)
	private Date startDate;

	@Column(name = "end_date", nullable = false)
	private Date endDate;

	@Column(name = "end_type", nullable = false)
	private String endType;

}
