package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "presentationgrade")
public class PresentationGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sspr_id", referencedColumnName = "id")
    private SubGrade sspr;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tcpr_id", referencedColumnName = "id")
    private SubGrade tcpr;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "otpr_id", referencedColumnName = "id")
    private SubGrade otpr;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "sprint_id", referencedColumnName = "id", nullable = false)
    private Sprint sprint;

    @Column(name = "value")
    private double value;

    public void calculateValue() {
        this.value = (sspr.getValue() + tcpr.getValue() + otpr.getValue()) / 3;
    }

    public boolean isCompleted() {
        return sspr.getStatus() == EvaluationStatus.COMPLETED &&
                tcpr.getStatus() == EvaluationStatus.COMPLETED &&
                otpr.getStatus() == EvaluationStatus.COMPLETED;
    }
}
