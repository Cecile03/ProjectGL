package com.example.backend.dto;

import com.example.backend.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInteract {

    private int id;
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String gender;
    private boolean bachelor;
    private List<String> roles;
    private String option;
    private String firstName;
    private String lastName;
    private Double gradePast;
    private String email;
    private String password;
    private int supervisor;

    @Getter
    @Setter
    private User user;

    //test
    @Setter
    private int teamId;

}
