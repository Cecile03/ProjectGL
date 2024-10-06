package com.example.backend.dto;

import com.example.backend.model.Criteria;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamDTO {

    //For the front
    private int id;
    private String name;
    private String status;
    private UserInteract supervisor;
    private List<UserInteract> users;
    private Criteria criteria;


    //From the front
    private List<UserInteract> students;
    private List<UserInteract> teachers;
    private String[] names;
    private int numberOfTeams;
    private int numberOfGirlsPerTeam;

}
