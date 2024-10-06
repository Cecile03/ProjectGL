package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class FlagDto {
    private int id;
    private int userId;
    private int team1Id;
    private int team2Id;
    private String comment;

    private LocalDateTime datetime;

}