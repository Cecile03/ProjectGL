package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name="teamorder")
public class TeamOrder{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @ManyToOne
    @JoinColumn(name="sprint_id", referencedColumnName="id")
    private Sprint sprint;
    @ManyToOne
    @JoinColumn(name="team_id", referencedColumnName="id")
    private Team team;

    @ManyToMany
    @OrderColumn
    private List<User> order;

    public void addAfterOrder(User user){
        order.add(user);
    }

    public void clean(){
        try{
            order.clear();
        } catch (Exception e){
            order = new ArrayList<>();
        }
    }
}