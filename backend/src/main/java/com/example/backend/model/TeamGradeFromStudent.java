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
@Table(name="teamgradefromstudent")
public class TeamGradeFromStudent {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private int id;
        @Column(name = "grade", nullable = false)
        private int grade;
        @ManyToOne
        @JoinColumn(name="team_noting_id", referencedColumnName="id") // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
        private Team teamNoting;
        @ManyToOne
        @JoinColumn(name="team_to_note_id", referencedColumnName="id") // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
        private Team teamToNote;
        @ManyToOne
        @JoinColumn(name="sprint_id", referencedColumnName="id") // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
        private Sprint sprint;
}
