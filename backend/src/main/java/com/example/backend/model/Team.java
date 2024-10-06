package com.example.backend.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@NamedQuery(name="Team.findAll", query="SELECT t FROM Team t")
@NamedQuery(name="Team.findById", query="SELECT t FROM Team t where t.id = :id")
@Table(name="team")
public class Team implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "status", nullable = false)
	private String status;

	@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
	@ManyToOne
	@JoinColumn(name = "teacher_id", referencedColumnName = "id")
	private User supervisor;

	@ManyToMany(mappedBy = "teams")
	private Set<User> users = new HashSet<>();

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "criteria_id", referencedColumnName = "id")
	private Criteria criteria;

	@Column(name = "validated", columnDefinition = "boolean default false")
	private boolean validated;

	public void setStatus(String status) {
		if(status.equals("publish")){
			setValidated(true);
		}
		this.status = status;
	}


}