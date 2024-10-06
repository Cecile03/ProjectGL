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
@Table(name="teamgrade")
public class TeamGrade{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private int id;
        @Column(name = "grade", nullable = false)
        private Double grade;
        @ManyToOne
        @JoinColumn(name="sprint_id", referencedColumnName="id", nullable = false) // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
        private Sprint sprint;
        @ManyToOne
        @JoinColumn(name="detail_id", referencedColumnName="id", nullable = false) // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
        private Detail detail;
        @ManyToOne
        @JoinColumn(name="team_id", referencedColumnName="id", nullable = false) // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
        private Team team;
        @ManyToOne
        @JoinColumn(name="user_id", referencedColumnName="id", nullable = false) // Spécifie la colonne de la clé étrangère et la colonne référencée dans la table liée
        private User evaluator;
}
