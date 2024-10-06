package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "bonus_malus")
public class BonusMalus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private float value;

    @Column(nullable = false)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BonusMalusStatus status;

    @Column
    private boolean isUnlimited;

    @ManyToOne
    @JoinColumn(name = "attributed_to", nullable = false)
    private User attributedTo;

    @ManyToOne
    @JoinColumn(name = "attributed_by", nullable = false)
    private User attributedBy;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;

    public enum BonusMalusStatus {
        @Column(length = 20)
        VALIDATED,
        @Column(length = 20)
        PENDING,
        @Column(length = 20)
        REJECTED
    }

}
