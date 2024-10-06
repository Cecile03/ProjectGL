package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.sql.Date;

/**
 * Classe Notification
 * Bean représentant un objet Notification.
 * @author Louis Peculier
 *
 */
@Setter
@Getter
@Entity
@NamedQuery(name="Notification.findAll", query="SELECT e FROM Notification e")
@Table(name="notification")
public class Notification implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	//@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private String type;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "date", nullable = false)
	@CreationTimestamp
	private Date date;

	@ManyToOne
	@JoinColumn(name="emitter_id", referencedColumnName="id") // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
	private User emitter;

	@ManyToOne
	@JoinColumn(name="receiver_id", referencedColumnName="id") // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
	private User receiver;

	@Column(name = "group_id")
	private int groupId;

	public enum Status {
		READ,
		UNREAD
	}

}
