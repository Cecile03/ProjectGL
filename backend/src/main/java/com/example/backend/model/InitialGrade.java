package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "initialgrade")
public class InitialGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prmo_id", referencedColumnName = "id")
    private SubGrade prmo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "spco_id", referencedColumnName = "id")
    private SubGrade spco;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "teso_id", referencedColumnName = "id")
    private SubGrade teso;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "supr_id", referencedColumnName = "id")
    private SubGrade supr;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "sprint_id", referencedColumnName = "id", nullable = false)
    private Sprint sprint;

    @Column(name = "value")
    private double value;

    public void calculateValue() {
        this.value = (prmo.getValue() + spco.getValue() + teso.getValue() + supr.getValue()) / 4;
    }

    public boolean isCompleted() {
        return prmo.getStatus() == EvaluationStatus.COMPLETED &&
                spco.getStatus() == EvaluationStatus.COMPLETED &&
                teso.getStatus() == EvaluationStatus.COMPLETED &&
                supr.getStatus() == EvaluationStatus.COMPLETED;
    }
}
