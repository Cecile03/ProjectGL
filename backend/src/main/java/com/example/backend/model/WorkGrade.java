package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "workgrade")
public class WorkGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ssbm_id", referencedColumnName = "id")
    private SubGrade ssbm;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tebm_id", referencedColumnName = "id")
    private SubGrade tebm;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "sprint_id", referencedColumnName = "id", nullable = false)
    private Sprint sprint;

    @Column(name = "value")
    private double value;

    public void calculateValue() {
        this.value = (ssbm.getValue() + tebm.getValue()) / 2;
    }

    public boolean isCompleted() {
        return ssbm.getStatus() == EvaluationStatus.COMPLETED &&
                tebm.getStatus() == EvaluationStatus.COMPLETED;
    }
}
