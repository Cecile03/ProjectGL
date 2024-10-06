package com.example.backend.dto;

import com.example.backend.model.Notification;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class NotificationDTO {

    //incoming
    private int id;
    private String type;
    private Notification.Status status;
    private String description;
    private int groupId;
    private int emitterId;
    private int receiverId;
    private Date date;

}
