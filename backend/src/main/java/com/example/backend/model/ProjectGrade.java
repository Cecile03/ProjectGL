package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "projectgrade")
public class ProjectGrade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	@Column(name = "value", nullable = false)
	private double value;

	@Column(name = "is_validated", columnDefinition = "boolean default false")
	private boolean isValidated;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "initialgrade_id", referencedColumnName = "id")
	private InitialGrade initialGrade;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "workgrade_id", referencedColumnName = "id")
	private WorkGrade workGrade;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "presentationgrade_id", referencedColumnName = "id")
	private PresentationGrade presentationGrade;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "sprint_id", referencedColumnName = "id", nullable = false)
	private Sprint sprint;

	public boolean areAllEvaluationsCompleted() {
		return initialGrade.isCompleted() && workGrade.isCompleted() && presentationGrade.isCompleted();
	}
}
