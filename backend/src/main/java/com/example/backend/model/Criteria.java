package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Classe Category
 * Bean représentant un objet Category.
 * @author Louis Peculier
 *
 */

@Entity
@Getter
@Setter
@Table(name="criteria")
public class Criteria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "number_of_girls", nullable = false)
    private int numberOfGirls;

    @Column(name = "number_of_teams", nullable = false)
    private int numberOfTeams;

    @Column(name = "number_of_bachelor", nullable = false)
    private int numberOfBachelor;

    @Column(name = "min_average_threshold", nullable = false)
    private double minAverageThreshold;

    @Override
    public String toString() {
        return "Criteria{" +
                "id=" + id +
                ", numberOfGirls=" + numberOfGirls +
                ", numberOfTeams=" + numberOfTeams +
                ", numberOfBachelor=" + numberOfBachelor +
                '}';
    }
}
