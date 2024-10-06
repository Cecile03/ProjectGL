package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BonusMalusDTO {

    private int id;
    private float value;
    private String comment;
    private String status;
    private boolean isUnlimited;
    private int attributedTo;
    private int attributedBy;
    private int teamId;
    private int sprintId;

}
