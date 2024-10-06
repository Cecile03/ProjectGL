package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "evaluation")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User evaluator;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "subgrade_id", referencedColumnName = "id", nullable = false)
    private SubGrade subGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EvaluationStatus status = EvaluationStatus.PENDING;

    @Column(name = "value")
    private Double value;

    public void setValue(Double value) {
        this.value = value;
        checkAndSetStatus();
    }

    private void checkAndSetStatus() {
        if (value != null) {
            status = EvaluationStatus.COMPLETED;
        }else {
            status = EvaluationStatus.PENDING;
        }
    }
}