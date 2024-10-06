package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO {

    private int id;
    private String title;
    private String content;
    private Date date;
    private UserSendDTO emitter;
    private TeamSendDTO team;
    private SprintDTO sprint;

}
