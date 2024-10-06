package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "userteam")
public class UserTeam {

    @EmbeddedId
    private UserTeamId id;

    @ManyToOne
    @MapsId("teamId")
    private Team team;

    @ManyToOne
    @MapsId("userId")
    private User user;

    public UserTeam(User user, Team team) {
        this.id = new UserTeamId(team.getId(), user.getId());
        this.team = team;
        this.user = user;
    }

    public UserTeam() {}

    // Getters and Setters

}

@Setter
@Getter
@Embeddable
class UserTeamId implements Serializable {

    private int teamId;

    private int userId;

    public UserTeamId() {}

    public UserTeamId(int teamId, int userId) {
        this.teamId = teamId;
        this.userId = userId;
    }


    // Getters and Setters

    // Equals and HashCode methods
}
