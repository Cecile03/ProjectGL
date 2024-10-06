package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "grade_types")
public class GradeTypes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private GradeTypesEnum name;

    @Column(name = "description", nullable = false)
    private String description;

    public GradeTypes() {
    }

    public GradeTypes(GradeTypesEnum name, String description) {
        this.name = name;
        this.description = description;
    }

    public GradeTypes(GradeTypesEnum gradeTypesEnum) {
        this.name = gradeTypesEnum;
        this.description = gradeTypesEnum.getDescription();
    }

    @PrePersist
    public void prePersist() {
        this.description = name.getDescription();
    }

    public enum GradeTypesEnum {
        PRMO("PRMO", 4, "Note d'équipe de gestion de projet"),
        SPCO("SPCO", 2, "Note d'équipe de respect de la solution prévue"),
        TESO("TESO", 3, "Note d'équipe de la solution technique présentée"),
        SUPR("SUPR", 5, "Note d'équipe de qualité du support de la présentation"),
        SSBM("SSBM", 7, "Bonus/Malus attribué par le référent"),
        TEBM("TEBM", 6, "Bonus/Malus attribué par l'équipe"),
        SSPR("SSPR", 8, "Moyenne des notes de présentation attribuées par les référents"),
        OTPR("OTPR", 1, "Moyenne des notes de présentation attribuées par les autres équipes"),
        TCPR("TCPR", 9, "Moyenne des notes de présentation attribuées par les coachs techniques");

        private final String name;
        private final int id;
        private final String description;

        GradeTypesEnum(String name, int id, String description) {
            this.name = name;
            this.id = id;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }
    }
}
