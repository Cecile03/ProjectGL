package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="user_flag")
public class UserFlag {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "flag_id", referencedColumnName = "id")
    private Flag flag;

    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "is_team_switched")
    private boolean isTeamSwitched;

    @Column(name = "is_validated")
    private Boolean isValidated;

    @Column(name = "canceled_string")
    private Integer canceledString;

    public boolean isTeamSwitched() {
        return isTeamSwitched;
    }

    public void setTeamSwitched(boolean teamSwitched) {
        isTeamSwitched = teamSwitched;
    }

    public Boolean getValidated() {
        return isValidated;
    }

    public void setValidated(Boolean validated) {
        isValidated = validated;
    }

}